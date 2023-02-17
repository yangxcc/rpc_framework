package yangxcc.test;

import yangxcc.client.RPCClient;
import yangxcc.client.RPCClientProxy;
import yangxcc.rpc.api.entity.HelloObject;
import yangxcc.rpc.api.service.HelloService;
import yangxcc.socket.SocketClient;

public class SocketTestClient {

    public static void main(String[] args) {
        RPCClient client = new SocketClient();
        RPCClientProxy proxy = new RPCClientProxy(client);
        HelloService service = proxy.getProxy(HelloService.class);

        System.out.println("准备发送");
        String res = service.hello(new HelloObject(100, "第一次测试"));
        System.out.println("发送完成");
        System.out.println(res);
    }
}
