package yangxcc.socket;

import lombok.extern.slf4j.Slf4j;
import yangxcc.common.enumdata.RPCError;
import yangxcc.common.exception.RPCException;
import yangxcc.nacos.NacosServiceRegistry;
import yangxcc.nacos.ServiceRegistry;
import yangxcc.netty.serializer.CommonSerializer;
import yangxcc.register.ServiceProvider;
import yangxcc.register.ServiceProviderImpl;
import yangxcc.server.RPCServer;
import yangxcc.server.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

@Slf4j
public class SocketServer implements RPCServer {
    private final ExecutorService threadPool;
    private final int THREAD_POLL_SIZE = 5;
    private final int MAXIMUM_POOL_SIZE = 20;
    private final long KEEP_ALIVE_TIME = 60;
    private final int WORK_QUEUE_SIZE = 100;

    private ServiceProvider serviceProvider;
    private ServiceRegistry serviceRegistry;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;

    private final int port;
    private final String host;

    public SocketServer(String host, int port) {
        this.port = port;
        this.host = host;

        this.serviceProvider = new ServiceProviderImpl();
        this.serviceRegistry = new NacosServiceRegistry();

        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(WORK_QUEUE_SIZE);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        requestHandler = new RequestHandler();
        threadPool = new ThreadPoolExecutor(THREAD_POLL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("服务器正在启动...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info(socket.toString());
                threadPool.execute(new RequestHandlerThread(socket, serviceProvider, requestHandler));
            }
        } catch (IOException e) {
            log.error("连接时有错误发生：", e);
        }
    }


    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if(serializer == null) {
            log.error("未设置序列化器");
            throw new RPCException(RPCError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addService(service);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
