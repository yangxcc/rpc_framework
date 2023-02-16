package yangxcc.socket;

import lombok.extern.slf4j.Slf4j;
import yangxcc.register.ServiceRegister;
import yangxcc.server.RPCServer;
import yangxcc.server.RequestHandler;

import java.io.IOException;
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

    private ServiceRegister serviceRegister;
    private RequestHandler requestHandler;

    public SocketServer(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;

        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(WORK_QUEUE_SIZE);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        requestHandler = new RequestHandler();
        threadPool = new ThreadPoolExecutor(THREAD_POLL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    @Override
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("服务器正在启动...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info(socket.toString());
                threadPool.execute(new RequestHandlerThread(socket, serviceRegister, requestHandler));
            }
        } catch (IOException e) {
            log.error("连接时有错误发生：", e);
        }
    }
}
