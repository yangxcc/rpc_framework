package yangxcc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import yangxcc.common.RPCRequest;
import yangxcc.netty.serializer.CommonSerializer;

import static yangxcc.common.enumdata.PackageType.REQUEST_PACK;
import static yangxcc.common.enumdata.PackageType.RESPONSE_PACK;

/**
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 */

public class CommonEncoder extends MessageToByteEncoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE; // 只是用来表明身份的一个数
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }


    @Override
    /**
     * @param o 需要编码的对象
     * @param bytebuf 字节流管道
     */
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(MAGIC_NUMBER);
        if (o instanceof RPCRequest) {
            byteBuf.writeInt(REQUEST_PACK.getCode());
        } else {
            byteBuf.writeInt(RESPONSE_PACK.getCode());
        }

        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
