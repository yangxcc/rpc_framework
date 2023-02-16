package yangxcc.common.enumdata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.omg.CORBA.UNKNOWN;

@AllArgsConstructor
@Getter
public enum RPCError {
    SERVICE_NOT_IMPLEMENT_ANY_METHODS("服务没有实现任何接口"),
    SERVICE_NOT_FOUND("服务未发现"),

    PACKAGE_NOT_MATCH("不能够识别的数据包"),
    PACKAGE_TYPE_NOT_FOUND("不符合规则的数据包类型"),
    UNKNOWN_DECODER("不能识别的反序列化器");

    private final String message;
}
