package yangxcc.server.test;

import yangxcc.netty.serializer.KryoSerializer;
import yangxcc.netty.server.NettyServer;
import yangxcc.rpc.api.service.HelloService;
import yangxcc.server.serviceImpl.HelloServiceImpl;

public class NettyTestServer {
    public static void main(String[] args) {
        HelloService service = new HelloServiceImpl();

        NettyServer nettyServer = new NettyServer("127.0.0.1", 10000);
        nettyServer.setSerializer(new KryoSerializer());
        nettyServer.publishService(service, HelloService.class);
        nettyServer.start();
    }
}
