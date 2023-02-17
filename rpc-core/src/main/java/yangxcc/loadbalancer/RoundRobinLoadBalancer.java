package yangxcc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 轮转算法，按照第一个，第二个，第三个...依次选择
 */
@Slf4j
public class RoundRobinLoadBalancer implements LoadBalancer {
    private int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        log.info("轮转负载均衡器选择了：{}", index);
        return instances.get(index++);
    }
}
