package yangxcc.nacos;

import java.net.InetSocketAddress;

public interface ServiceRegistry {
    /**
     * 将服务名和其IP地址注册进注册中心（服务端使用）
     * @param serviceName
     * @param address
     */
    void register(String serviceName, InetSocketAddress address);

    /**
     * 根据服务名称得到IP地址（客户端使用）
     * @param name
     * @return
     */
    InetSocketAddress getAddressByServiceName(String name);
}
