package yangxcc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

@Slf4j
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Instance select(List<Instance> instances) {
        int index = new Random().nextInt(instances.size());
        log.info("随机负载均衡器选择了：{}", index);
        return instances.get(index);
    }
}
