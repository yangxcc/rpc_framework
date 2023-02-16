package yangxcc.socket;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import yangxcc.client.RPCClient;
import yangxcc.common.RPCRequest;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@AllArgsConstructor
public class SocketClient implements RPCClient {
    private final String host;
    private final int port;

    @Override
    @SneakyThrows
    public Object sendRequest(RPCRequest rpcRequest) {
        Socket socket = new Socket(host, port);
        // 必须先getOutputStream，要和服务端相反，避免死锁！！！！
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        outputStream.writeObject(rpcRequest);
        outputStream.flush();

        return inputStream.readObject();
    }
}
