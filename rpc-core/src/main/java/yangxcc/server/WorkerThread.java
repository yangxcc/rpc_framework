package yangxcc.server;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCRequest;
import yangxcc.common.RPCResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

@Slf4j
public class WorkerThread implements Runnable {
    private Socket socket;
    private Object service;

    public WorkerThread(Socket socket, Object service) {
        log.info("实例化工作线程...");
        this.socket = socket;
        this.service = service;
    }

    @Override
    @SneakyThrows
    public void run() {
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
        RPCRequest request = (RPCRequest) inputStream.readObject();
        log.info("接下来要去调用服务端该方法的实现了...");
        // 根据请求去调用本地的方法
        Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
        Object returnData = method.invoke(service, request.getParameters());  // 执行对象的目标方法
        log.info("处理请求成功");

        // 写入输出流
        outputStream.writeObject(RPCResponse.success(returnData));  // 这里要将returnData转成RPCResponse类型
        outputStream.flush();
    }
}
