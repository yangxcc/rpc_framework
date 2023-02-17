package yangxcc.server.test;

import yangxcc.register.ServiceProviderImpl;
import yangxcc.register.ServiceProvider;
import yangxcc.rpc.api.service.HelloService;
import yangxcc.server.RPCServer;
import yangxcc.server.serviceImpl.HelloServiceImpl;
import yangxcc.socket.SocketServer;

public class SocketTestServer {

    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();

//        ServiceProvider serviceProvider = new ServiceProviderImpl();
//        serviceProvider.addService(service);

        RPCServer server = new SocketServer("127.0.0.1", 10000);
        server.start();
    }
}
