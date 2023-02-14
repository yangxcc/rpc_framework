package yangxcc.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 服务端，远程方法调用的提供端
 */

@Slf4j
public class RPCServer {
    // 创建一个线程池
    private final ExecutorService threadPool;

    public RPCServer() {
        int poolSize = 5;
        int maximumPoolSize = 20;
        long keepAliveTime = 60;

        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        threadPool = new ThreadPoolExecutor(poolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    // 提供一个注册方法
    public void register(Object service, int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("服务器正在启动...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                log.info("客户端连接！Ip为：" + socket.getInetAddress());
                log.info(String.valueOf(socket.getInetAddress()));
                log.info(String.valueOf(socket.getPort()));
                log.info(socket.toString());
                threadPool.execute(new WorkerThread(socket, service));
            }
        } catch (IOException e) {
            log.error("连接时有错误发生：", e);
        }
    }
}
