package yangxcc.client;

import yangxcc.common.RPCRequest;
import yangxcc.netty.serializer.CommonSerializer;

public interface RPCClient {
    Object sendRequest(RPCRequest rpcRequest);

    void setSerializer(CommonSerializer serializer);
}
