package yangxcc.server.test;

import yangxcc.rpc.api.service.HelloService;
import yangxcc.server.RPCServer;
import yangxcc.server.serviceImpl.HelloServiceImpl;

public class TestServer {

    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();

        RPCServer server = new RPCServer();
        server.register(service, 9000);
    }
}
