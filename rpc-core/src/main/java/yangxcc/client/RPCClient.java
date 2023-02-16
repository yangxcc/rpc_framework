package yangxcc.client;

import yangxcc.common.RPCRequest;

public interface RPCClient {
    Object sendRequest(RPCRequest rpcRequest);
}
