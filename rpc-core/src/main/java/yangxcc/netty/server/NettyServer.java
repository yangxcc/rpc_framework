package yangxcc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import yangxcc.codec.CommonDecoder;
import yangxcc.codec.CommonEncoder;
import yangxcc.common.enumdata.RPCError;
import yangxcc.common.exception.RPCException;
import yangxcc.loadbalancer.LoadBalancer;
import yangxcc.loadbalancer.RoundRobinLoadBalancer;
import yangxcc.nacos.NacosServiceRegistry;
import yangxcc.nacos.ServiceRegistry;
import yangxcc.nacos.hook.ShutdownHook;
import yangxcc.netty.serializer.CommonSerializer;
import yangxcc.netty.serializer.KryoSerializer;
import yangxcc.register.ServiceProviderImpl;
import yangxcc.register.ServiceProvider;
import yangxcc.server.RPCServer;

import java.net.InetSocketAddress;

@Slf4j
public class NettyServer implements RPCServer {
    private final String host;
    private final int port;
    private final ServiceProvider serviceProvider;
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceProvider = new ServiceProviderImpl();
        serviceRegistry = new NacosServiceRegistry(null);
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
//                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 责任链模式，编码器，解码器，处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(host, port).sync();
            ShutdownHook.getShutdownHook().clearAllRegisteredServiceHook();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if (serializer == null) {
            log.error("服务端未设置序列化器");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addService(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
