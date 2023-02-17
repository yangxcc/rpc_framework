package yangxcc.nacos.hook;

import lombok.extern.slf4j.Slf4j;
import yangxcc.common.factory.ThreadPoolFactory;
import yangxcc.common.utils.NacosUtils;

import java.util.concurrent.ExecutorService;

@Slf4j
public class ShutdownHook {
    private final ExecutorService threadPool = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");

    /**
     * 使用单例模式创建一个hook对象
     */
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void clearAllRegisteredServiceHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtils.clearAllRegisteredService();
            threadPool.shutdown();
        }));
    }
}
