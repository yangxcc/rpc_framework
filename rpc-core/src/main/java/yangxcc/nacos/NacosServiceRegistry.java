package yangxcc.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.enumdata.RPCError;
import yangxcc.common.exception.RPCException;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {
    private static final String SERVER_ADDR = "192.168.95.132:8848";
    private static final NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
            log.info("成功连接nacos");
        } catch (NacosException e) {
            log.error("连接nacos时有错误发生：{}", e.toString());
            throw new RPCException(RPCError.FAIL_CONNECT_TO_NACOS);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try {
            namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
            log.info("服务注册成功:{},{},{}", serviceName, address.getHostName(), address.getPort());
        } catch (NacosException e) {
            log.error("向nacos注册服务时有错误发生：{}", e.toString());
            throw new RPCException(RPCError.REGISTER_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress getAddressByServiceName(String name) {
        try {
            List<Instance> allInstances = namingService.getAllInstances(name);
            /**
             * 通过 getAllInstance 获取到某个服务的所有提供者列表后，需要选择一个，
             * 这里就涉及了负载均衡策略，这里我们先选择第 0 个，负载均衡内容后面再说
             */
            Instance instance = allInstances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());

        } catch (NacosException e) {
            log.error("向nacos请求服务时出错：{}", e.toString());
            throw new RPCException(RPCError.GET_SERVICE_FROM_NACOS_FAILED);
        }
    }
}
