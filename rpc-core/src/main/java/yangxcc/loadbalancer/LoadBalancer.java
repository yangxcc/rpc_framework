package yangxcc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface LoadBalancer {
    /**
     * 选择一个instance
     */
    Instance select(List<Instance> instances);
}
