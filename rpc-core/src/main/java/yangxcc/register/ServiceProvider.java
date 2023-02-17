package yangxcc.register;

public interface ServiceProvider {
    /**
     * 注册服务
     */
    <T> void addService(T service);

    /**
     * 根据服务名称获取服务实体
     */
    Object getService(String serviceName);
}
