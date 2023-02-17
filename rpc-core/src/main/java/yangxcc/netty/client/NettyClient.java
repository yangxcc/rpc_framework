package yangxcc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import yangxcc.client.RPCClient;
import yangxcc.codec.CommonDecoder;
import yangxcc.codec.CommonEncoder;
import yangxcc.common.RPCRequest;
import yangxcc.common.RPCResponse;
import yangxcc.common.enumdata.RPCError;
import yangxcc.common.exception.RPCException;
import yangxcc.loadbalancer.LoadBalancer;
import yangxcc.loadbalancer.RoundRobinLoadBalancer;
import yangxcc.nacos.NacosServiceRegistry;
import yangxcc.nacos.ServiceRegistry;
import yangxcc.netty.serializer.CommonSerializer;
import yangxcc.netty.serializer.KryoSerializer;

import java.net.InetSocketAddress;

@Slf4j
public class NettyClient implements RPCClient {
    private static final Bootstrap bootstrap;
    private ServiceRegistry serviceRegistry;

    private CommonSerializer serializer;

    public NettyClient(LoadBalancer loadBalancer) {
        this.serviceRegistry = new NacosServiceRegistry(loadBalancer);
}

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }


    @Override
    public Object sendRequest(RPCRequest rpcRequest) {
        if (serializer == null) {
            log.error("客户端未设置序列化器");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        try {
            InetSocketAddress address = serviceRegistry.getAddressByServiceName(rpcRequest.getInterfaceName());
            log.info(address.toString());

            Channel channel = ChannelProvider.get(address, serializer);
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()) {
                        log.info(String.format("向客户端发送消息 %s 成功", rpcRequest.toString()));
                    } else {
                        log.error("向客户端发送消息时出现问题:" + future1.cause());
                    }
                });

                channel.closeFuture().sync();
                // 从ctx中读取出响应数据
                AttributeKey<RPCResponse> key = AttributeKey.valueOf("rpcResponse");
                RPCResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
