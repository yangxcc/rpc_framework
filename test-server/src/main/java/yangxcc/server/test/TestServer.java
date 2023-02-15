package yangxcc.server.test;

import lombok.extern.slf4j.Slf4j;
import yangxcc.register.DefaultServiceRegisterCenter;
import yangxcc.register.ServiceRegister;
import yangxcc.rpc.api.service.HelloService;
import yangxcc.server.RPCServer;
import yangxcc.server.serviceImpl.HelloServiceImpl;

public class TestServer {

    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();

        ServiceRegister serviceRegister = new DefaultServiceRegisterCenter();
        serviceRegister.register(service);

        RPCServer server = new RPCServer(serviceRegister);
        server.start(9000);
    }
}
