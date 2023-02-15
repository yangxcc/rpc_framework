在`first-commit`中服务的注册和服务器启动是高度耦合的，所以`second-commit`的目的就是将服务注册和服务器启动分离开。



首先，我们注册中心的功能主要有两个，注册服务和根据服务名称获得服务实体，具体实现见[DefaultServiceRegisterCenter](../rpc-core/src/main/java/yangxcc/register/DefaultServiceRegisterCenter.java)

```java
public interface ServiceRegister {
    /**
     * 注册服务
     */
    <T> void register(T service);

    /**
     * 根据服务名称获取服务实体
     */
    Object getService(String serviceName);
}
```

实现过程中需要注意反射的几个方法

- `getCanonicalName()`获得的是当前类的类路径名称，比如`HelloService service = new HelloServiceImpl(); service.getClass().getCanonicalName()`得到的结果是`yangxcc.server.serviceImpl.HelloServiceImpl`
- `getInterfaces()`获得的是当前类实现的接口，返回结果是`Class<?>[]`
- 如果根据`Method`类型的对象获得他所在类的名称：`method.getDeclaringClass().getName()`



接下来，我们需要修改一下服务器处理请求的步骤，当前的流程是：将service和port同时传入并启动服务器，这种情况下只能够处理一个service，所以我们需要修改的地方：在服务器实例化时就将注册服务表传入（注册服务表中有多个service，而且将服务器启动和服务注册分离开）,服务器启动只需要一个端口号即可，具体如下：

```java
public class TestServer {

    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();

        ServiceRegister serviceRegister = new DefaultServiceRegisterCenter();
        serviceRegister.register(service);

        RPCServer server = new RPCServer(serviceRegister);
        server.start(9000);
    }
}
```



接下来，我们就要开始处理请求的处理流程了，很明显，我们需要在真正处理请求时根据服务名称把服务注册表中的服务解析出来

```java
ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
RPCRequest request = (RPCRequest) inputStream.readObject();
log.info("取到了请求..." + request.toString());

// 取出服务对象
log.info(request.getInterfaceName());
Object service = serviceRegister.getService(request.getInterfaceName());
// 拿到对象和请求之后开始真正处理请求(method.invoke那一套)
Object resultData = requestHandler.handle(service, request);

outputStream.writeObject(RPCResponse.success(resultData));
outputStream.flush();
```

具体见[RequestHandlerThread](../rpc-core/src/main/java/yangxcc/server/RequestHandlerThread.java)