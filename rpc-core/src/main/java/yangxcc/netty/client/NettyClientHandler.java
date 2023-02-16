package yangxcc.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCResponse;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RPCResponse> {
    /** 解析服务器返回的响应消息 */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCResponse rpcResponse) throws Exception {
        log.info(String.format("客户端收到消息：%s", rpcResponse.toString()));
        // 将响应数据RPCResponse放到ctx中， key是rpcResponse
        AttributeKey<RPCResponse> key = AttributeKey.valueOf("rpcResponse");
        channelHandlerContext.channel().attr(key).set(rpcResponse);
        channelHandlerContext.close();
    }
}
