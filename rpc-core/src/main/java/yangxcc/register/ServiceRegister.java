package yangxcc.register;

public interface ServiceRegister {
    /**
     * 注册服务
     */
    <T> void register(T service);

    /**
     * 根据服务名称获取服务实体
     */
    Object getService(String serviceName);
}
