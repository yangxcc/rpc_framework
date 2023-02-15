package yangxcc.server;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import yangxcc.common.RPCRequest;

import java.lang.reflect.Method;

@NoArgsConstructor
public class RequestHandler {

    @SneakyThrows
    public Object handle(Object service, RPCRequest request) {
        Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
        return method.invoke(service, request.getParameters());
    }
}
