package yangxcc.test;

import yangxcc.client.RPCClient;
import yangxcc.client.RPCClientProxy;
import yangxcc.netty.client.NettyClient;
import yangxcc.rpc.api.entity.HelloObject;
import yangxcc.rpc.api.service.HelloService;

public class NettyTestClient {
    public static void main(String[] args) {
        RPCClient client = new NettyClient("127.0.0.1", 10000);
        RPCClientProxy proxy = new RPCClientProxy(client);
        HelloService service = proxy.getProxy(HelloService.class);

        System.out.println("准备发送");
        String res = service.hello(new HelloObject(100, "第一次测试"));
        System.out.println("发送完成");
        System.out.println(res);
    }
}
