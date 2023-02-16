这次commit的目的是从原始的socket传输改成由netty传输

为了达到这个目的，我们首先把服务端和客户端的方法抽象出来，如下：

```java
// RPCClient
public interface RPCClient {
    Object sendRequest(RPCRequest rpcRequest);
}

// RPCServer
public interface RPCServer {
    void start(int port); // 需要指定监听的端口
}
```

这里作者有一个小改动，那就是在服务注册的时候把`serviceMap`和`registeredService`设置成了static类型了，这样的话，我们在创建`NettyServer`的时候就无需传入`serviceRegister`，因为所有的`ServiceRegister`对象都能够取出相同的`serviceMap`和`registeredService`（static用法），其他对于当前存在的`SocketClient`和`SocketServer`不用做太大的修改	



接下来，我们去实现一下[NettyServer](../rpc-core/src/main/java/yangxcc/netty/server/NettyServer.java)和[NettyClient](../rpc-core/src/main/java/yangxcc/netty/client/NettyClient.java)，在这里面的主要问题是Netty服务启动的时候处理器的顺序

```java
// 客户端
bootstrap.group(group)
    .channel(NioSocketChannel.class)
    .option(ChannelOption.SO_KEEPALIVE, true)
    .handler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new CommonDecoder())
                .addLast(new CommonEncoder(new JSONSerializer()))
                .addLast(new NettyClientHandler());
        }
    });

// 服务器
.childHandler(new ChannelInitializer<SocketChannel>() {
    // 责任链模式，编码器，解码器，处理器
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new CommonEncoder(new JSONSerializer()));
        pipeline.addLast(new CommonDecoder());
        pipeline.addLast(new NettyServerHandler());
    }
});
```

按照[一文搞懂Netty中Handler的执行顺序_买糖买板栗的博客-CSDN博客_netty handler执行顺序](https://blog.csdn.net/zhengchao1991/article/details/103583766)的说法，存在以下两种情况：

- `ctx.writeAndFlush`只会从当前的handler位置开始，往前找outbound执行
- `ctx.pipeline().writeAndFlush`与`ctx.channel().writeAndFlush`会从tail的位置开始，往前找outbound执行

> Netty中的所有handler都实现自ChannelHandler接口。按照输出输出来分，分为ChannelInboundHandler、ChannelOutboundHandler两大类。ChannelInboundHandler对从客户端发往服务器的报文进行处理，一般用来执行解码、读取客户端数据、进行业务处理等；ChannelOutboundHandler对从服务器发往客户端的报文进行处理，一般用来进行编码、发送报文到客户端。



### 自定义协议和解编码器

```
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
```

- `Magic Number`只是用来标记一个自定义数据包，无特殊含义
- `Package Type`用来标记数据包的类型，是`RPCRequest`包还是`RPCResponse`包
- `Serializer Type`用来定义序列化器的类型，是JSON还是Thirft、Protobf还是其他的...
- `Data Length`用来定义数据的长度，避免TCP的粘包问题

> 这里我们是把协议包编码成json格式，借助的是Jackson工具包



CommonEncoder 的工作很简单，就是把 RpcRequest 或者 RpcResponse 包装成协议包。 根据上面提到的协议格式，将各个字段写到管道里就可以了，这里serializer.getCode() 获取序列化器的编号，之后使用传入的序列化器将请求或响应包序列化为字节数组写入管道即可。具体见[CommonEncoder](../rpc-core/src/main/java/yangxcc/codec/CommonEncoder.java)



同理，CommonDecoder 的工作就是把协议包反序列成RpcRequest 或者 RpcResponse。 根据上面提到的协议格式，将各个字段从管道里读取出来，主要工作包括对一些字段的校验，比较重要的就是取出序列化器的编号，以获得正确的反序列化方式，并且读入 length 字段来确定数据包的长度（防止粘包），最后读入正确大小的字节数组，反序列化成对应的对象。具体见[CommonDecoder](../rpc-core/src/main/java/yangxcc/codec/CommonDecoder.java)

这里需要注意，在反序列化`RPCRequest`的时候，由于`RPCRequest`中的参数列表字段的类型是`Object[]`，反序列化时序列化器会根据字段类型进行反序列化，而 Object 就是一个十分模糊的类型，会出现反序列化失败的现象，所以这时候就需要RPCRequest中的另一个参数`parameterTypes`来获取到来获取参数列表中元素对应的参数类型，如下

```java
@SneakyThrows
private Object handleRequest(Object obj) {
    RPCRequest request = (RPCRequest) obj;
    Class<?>[] parameterTypes = request.getParameterTypes();

    for (int i = 0; i < parameterTypes.length; i++) {
        // 数据类型发生了改变，比如int类型的1变成了“1”
        if (!parameterTypes[i].isAssignableFrom(request.getParameters()[i].getClass())) {
            byte[] bytes = objectMapper.writeValueAsBytes(request.getParameters()[i]);
            request.getParameters()[i] = objectMapper.readValue(bytes, parameterTypes[i]);
        }
    }

    return request;
}
```



至于后面的[NettyClientHandler]()和[NettyServerHandler]()，因为序列化和反序列化的任务已经有前面的序列化器完成了，所以`NettyServerHandler`只需要专注于执行请求处理逻辑即可，`NetttyClientHandler`则需要专注于将响应数据放到ctx中返回出去



