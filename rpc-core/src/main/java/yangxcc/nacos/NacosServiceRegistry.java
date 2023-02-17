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

import yangxcc.common.enumdata.NacosInfo;
import yangxcc.common.utils.NacosUtils;
import yangxcc.loadbalancer.LoadBalancer;
import yangxcc.loadbalancer.RandomLoadBalancer;

@Slf4j
public class NacosServiceRegistry implements ServiceRegistry {
    private static final NamingService namingService;
    private final LoadBalancer loadBalancer;

    public NacosServiceRegistry(LoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            this.loadBalancer = new RandomLoadBalancer();
        } else {
            this.loadBalancer = loadBalancer;
        }
    }

    static {
        namingService = NacosUtils.getNamingService();
    }

    @Override
    public void register(String serviceName, InetSocketAddress address) {
        NacosUtils.registerService(serviceName, address);
    }

    @Override
    public InetSocketAddress getAddressByServiceName(String name) {
        try {
            List<Instance> allInstances = namingService.getAllInstances(name);
            /**
             * 通过 getAllInstance 获取到某个服务的所有提供者列表后，需要选择一个，
             * 这里就涉及了负载均衡策略，这里我们先选择第 0 个，负载均衡内容后面再说
             */
            Instance instance = loadBalancer.select(allInstances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());

        } catch (NacosException e) {
            log.error("向nacos请求服务时出错：{}", e.toString());
            throw new RPCException(RPCError.GET_SERVICE_FROM_NACOS_FAILED);
        }
    }
}
