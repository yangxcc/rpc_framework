package yangxcc.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCRequest;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class RPCClient {

    @SneakyThrows  // 异常处理注解，省去对非运行时异常处理的try catch语句
    public Object sendRequest(String host, int port, RPCRequest rpcRequest) {
        Socket socket = new Socket(host, port);
        // 必须先getOutputStream，要和服务端相反，避免死锁！！！！
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        outputStream.writeObject(rpcRequest);
        outputStream.flush();

        return inputStream.readObject();
    }
}
