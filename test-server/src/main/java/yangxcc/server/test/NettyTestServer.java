package yangxcc.server.test;

import yangxcc.netty.server.NettyServer;
import yangxcc.register.DefaultServiceRegisterCenter;
import yangxcc.register.ServiceRegister;
import yangxcc.rpc.api.service.HelloService;
import yangxcc.server.serviceImpl.HelloServiceImpl;

public class NettyTestServer {
    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();

        ServiceRegister serviceRegister = new DefaultServiceRegisterCenter();
        serviceRegister.register(service);

        NettyServer nettyServer = new NettyServer();
        nettyServer.start(10000);
    }
}
