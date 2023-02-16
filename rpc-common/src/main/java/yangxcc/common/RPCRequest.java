package yangxcc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor   // 需要有一个无参构造器，否则序列化会出错
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
