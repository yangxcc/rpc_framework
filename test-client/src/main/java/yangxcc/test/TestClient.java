package yangxcc.test;

import yangxcc.client.RPCClientProxy;
import yangxcc.rpc.api.entity.HelloObject;
import yangxcc.rpc.api.service.HelloService;

public class TestClient {

    public static void main(String[] args) {
        RPCClientProxy proxy = new RPCClientProxy("127.0.0.1", 9000);
        HelloService service = proxy.getProxy(HelloService.class);

        System.out.println("准备发送");
        String res = service.hello(new HelloObject(100, "第一次测试"));
        System.out.println("发送完成");
        System.out.println(res);
    }
}
