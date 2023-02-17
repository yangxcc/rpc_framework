package yangxcc.common.utils;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.esotericsoftware.minlog.Log;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.enumdata.NacosInfo;
import yangxcc.common.enumdata.RPCError;
import yangxcc.common.exception.RPCException;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 用来管理Nacos连接等操作的工具类
 */
@Slf4j
public class NacosUtils {
    private static final String nacosAddress = NacosInfo.NACOS_ADDRESS.getHost() + ":" + NacosInfo.NACOS_ADDRESS.getPort();

    private static final NamingService namingService;

    private static final HashSet<String> registeredServices = new HashSet<>();
    private static InetSocketAddress address;

    static {
        namingService = getNamingService();
    }


    public static NamingService getNamingService() {
        try {
            return NamingFactory.createNamingService(nacosAddress);
        } catch (NacosException e) {
            log.error("连接nacos时有错误发生：{}", e.toString());
            throw new RPCException(RPCError.FAIL_CONNECT_TO_NACOS);
        }
    }

    public static void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            log.info(inetSocketAddress.getHostName());
            log.info(String.valueOf(inetSocketAddress.getPort()));
            namingService.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
            registeredServices.add(serviceName);
            /**
             * 因为这个address是服务器启动时传过来的，所以他只有一个ip和端口，不会是多个ip和端口
             * 对应到现实场景中，应该是多个接口对应一个实现类，即多个服务对应一个ip+port
             */
            address = inetSocketAddress;
            log.info("服务注册成功:{},{},{}", serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            log.error("向nacos注册服务时有错误发生：{}", e.toString());
            throw new RPCException(RPCError.REGISTER_SERVICE_FAILED);
        }
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * 清空所有的注册方法
     */
    public static void clearAllRegisteredService() {
        if (!registeredServices.isEmpty() && address != null) {
            String host = address.getHostName();
            int port = address.getPort();
            for (String serviceName : registeredServices) {
                try {
                    namingService.deregisterInstance(serviceName, host, port);
                    log.info("服务注销：{}", serviceName);
                } catch (NacosException e) {
                    log.error("服务{}注销失败，原因是{}", serviceName, e.toString());
                }
            }
        }
    }
}
