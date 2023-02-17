package yangxcc.server;

import yangxcc.netty.serializer.CommonSerializer;

/**
 * 服务端，远程方法调用的提供端
 */

public interface RPCServer {
    void start();

    <T> void publishService(Object service, Class<T> serviceClass);

    void setSerializer(CommonSerializer serializer);
}