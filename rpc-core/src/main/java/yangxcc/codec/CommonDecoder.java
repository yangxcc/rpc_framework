package yangxcc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCRequest;
import yangxcc.common.RPCResponse;
import yangxcc.common.enumdata.PackageType;
import yangxcc.common.enumdata.RPCError;
import yangxcc.common.exception.RPCException;
import yangxcc.netty.serializer.CommonSerializer;

import java.util.List;

/**
 * +---------------+---------------+-----------------+-------------+
 * |  Magic Number |  Package Type | Serializer Type | Data Length |
 * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
 * +---------------+---------------+-----------------+-------------+
 * |                          Data Bytes                           |
 * |                   Length: ${Data Length}                      |
 * +---------------------------------------------------------------+
 */

@Slf4j
public class CommonDecoder extends ReplayingDecoder {
    private static final int MAGIC_NUMBER = 0xCAFEBABE; // 只是用来表明身份的一个数

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNumber = byteBuf.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            log.error("这是一个不能够识别的数据包");
            throw new RPCException(RPCError.PACKAGE_NOT_MATCH);
        }

        int packageType = byteBuf.readInt();
        Class<?> clazz;
        if (packageType == PackageType.REQUEST_PACK.getCode()) {
            clazz = RPCRequest.class;
        } else if (packageType == PackageType.RESPONSE_PACK.getCode()) {
            clazz = RPCResponse.class;
        } else {
            log.error("无法判断数据包类型");
            throw new RPCException(RPCError.PACKAGE_TYPE_NOT_FOUND);
        }

        int serializeType = byteBuf.readInt();
        CommonSerializer serializer = CommonSerializer.getSerializerByCode(serializeType);
        if (serializer == null) {
            log.error("未知反序列器");
            throw new RPCException(RPCError.UNKNOWN_DECODER);
        }

        int dataLength = byteBuf.readInt();
        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);

        Object obj = serializer.deserialize(bytes, clazz);
        list.add(obj);
    }
}
