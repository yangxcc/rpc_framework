package yangxcc.socket;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCRequest;
import yangxcc.common.RPCResponse;
import yangxcc.register.ServiceProvider;
import yangxcc.server.RequestHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class RequestHandlerThread implements Runnable {
    private Socket socket;
    private ServiceProvider serviceProvider;
    private RequestHandler requestHandler;

    public RequestHandlerThread(Socket socket, ServiceProvider serviceProvider, RequestHandler requestHandler) {
        this.socket = socket;
        this.serviceProvider = serviceProvider;
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
        Object service = serviceProvider.getService(request.getInterfaceName());
        // 拿到对象和请求之后开始真正处理请求
        Object resultData = requestHandler.handle(service, request);

        outputStream.writeObject(RPCResponse.success(resultData));
        outputStream.flush();
    }
}
