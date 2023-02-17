package yangxcc.register;

import lombok.extern.slf4j.Slf4j;
import yangxcc.common.enumdata.RPCError;
import yangxcc.common.exception.RPCException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Slf4j
public class ServiceProviderImpl implements ServiceProvider {
    // 无需使用ConcurrentHashMap？？？
    // 服务名称（接口名）对应的服务实体对象
    private static final HashMap<String, Object> serviceMap = new HashMap<>();
    // 已经注册过的类
    private static final HashSet<String> registeredService = new HashSet<>();

    @Override
    public <T> void addService(T service) {
        log.info("启动服务注册");
        // 获取类名 yangxcc.server.serviceImpl.HelloServiceImpl
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        log.info(serviceName);

        // 获得对象实现的所有接口 [interface yangxcc.rpc.api.service.HelloService]，这样才能够和客户端对应上，因为客户端没有实现类，只能通过动态代理的方式获得接口的对象
        Class<?>[] interfaces = service.getClass().getInterfaces();
        log.info(Arrays.toString(interfaces));

        if (interfaces.length == 0) {
            throw new RPCException(RPCError.SERVICE_NOT_IMPLEMENT_ANY_METHODS);
        }

        for (Class<?> in : interfaces) {
            serviceMap.put(in.getCanonicalName(), service);
        }

        log.info("服务注册完毕");
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);
        }

        return service;
    }
}
