package yangxcc.client;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCRequest;
import yangxcc.common.RPCResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class RPCClientProxy implements InvocationHandler {
    private final RPCClient rpcClient;

    /**
     * 第一个 <T> 表示泛型
     * 第二个 T 表示返回的是T类型的数据
     * 第三个 T 表示的是限制参数类型为 T
     */
    @SuppressWarnings("unchecked") // 告诉编译器忽略 unchecked 警告信息，如使用List，ArrayList等未进行参数化产生的警告信息。
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RPCRequest rpcRequest = new RPCRequest(method.getDeclaringClass().getName(), method.getName(),
                args, method.getParameterTypes());

        log.info("准备发送请求");
        return rpcClient.sendRequest(rpcRequest);
    }
}
