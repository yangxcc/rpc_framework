package yangxcc.netty.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCRequest;
import yangxcc.common.RPCResponse;
import yangxcc.register.ServiceProviderImpl;
import yangxcc.register.ServiceProvider;
import yangxcc.server.RequestHandler;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    private static ServiceProvider serviceProvider;
    private static RequestHandler requestHandler;

    static {
        serviceProvider = new ServiceProviderImpl();
        requestHandler = new RequestHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCRequest rpcRequest) throws Exception {
        log.info("服务器接收到请求：{}", rpcRequest.toString());
        String serviceName = rpcRequest.getInterfaceName();
        log.info("服务名：{}", serviceName);
        Object service = serviceProvider.getService(serviceName);
        Object responseData = requestHandler.handle(service, rpcRequest);
        ChannelFuture future = channelHandlerContext.writeAndFlush(RPCResponse.success(responseData));
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
