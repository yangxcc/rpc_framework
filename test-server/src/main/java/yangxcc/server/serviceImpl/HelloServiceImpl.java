package yangxcc.server.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import yangxcc.rpc.api.entity.HelloObject;
import yangxcc.rpc.api.service.HelloService;

@Slf4j
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(HelloObject ho) {
        log.info("接收到客户端发送过来的请求，下面进行处理....");
        return String.format("id: %d, 收到的信息: %s", ho.getId(), ho.getMessage());
    }
}
