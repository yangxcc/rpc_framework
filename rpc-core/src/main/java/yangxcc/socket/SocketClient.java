package yangxcc.socket;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import yangxcc.client.RPCClient;
import yangxcc.common.RPCRequest;
import yangxcc.nacos.NacosServiceRegistry;
import yangxcc.nacos.ServiceRegistry;
import yangxcc.netty.serializer.CommonSerializer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@AllArgsConstructor
public class SocketClient implements RPCClient {

    private ServiceRegistry serviceRegistry;

    public SocketClient() {
        this.serviceRegistry = new NacosServiceRegistry();
    }
    @Override
    @SneakyThrows
    public Object sendRequest(RPCRequest rpcRequest) {
        InetSocketAddress address = serviceRegistry.getAddressByServiceName(rpcRequest.getInterfaceName());
        Socket socket = new Socket(address.getHostName(), address.getPort());
        // 必须先getOutputStream，要和服务端相反，避免死锁！！！！
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

        outputStream.writeObject(rpcRequest);
        outputStream.flush();

        return inputStream.readObject();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {

    }
}
