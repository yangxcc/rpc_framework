package yangxcc.common;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
@Builder
public class RPCRequest implements Serializable {
    /**
     * 接口名字
     */
    private String interfaceName;

    /**
     * 方法名字
     */
    private String methodName;

    /**
     * 参数列表
     */
    private Object[] parameters;

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
}
