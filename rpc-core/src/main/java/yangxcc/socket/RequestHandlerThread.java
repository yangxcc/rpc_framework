package yangxcc.socket;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCRequest;
import yangxcc.common.RPCResponse;
import yangxcc.register.ServiceRegister;
import yangxcc.server.RequestHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

@Slf4j
public class RequestHandlerThread implements Runnable {
    private Socket socket;
    private ServiceRegister serviceRegister;
    private RequestHandler requestHandler;

    public RequestHandlerThread(Socket socket, ServiceRegister serviceRegister, RequestHandler requestHandler) {
        this.socket = socket;
        this.serviceRegister = serviceRegister;
        this.requestHandler = requestHandler;
    }

    @Override
    @SneakyThrows
    public void run() {
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        RPCRequest request = (RPCRequest) inputStream.readObject();
        log.info("取到了请求..." + request.toString());

        // 取出服务对象
        log.info(request.getInterfaceName());
        Object service = serviceRegister.getService(request.getInterfaceName());
        // 拿到对象和请求之后开始真正处理请求
        Object resultData = requestHandler.handle(service, request);

        outputStream.writeObject(RPCResponse.success(resultData));
        outputStream.flush();
    }
}
