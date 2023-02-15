package yangxcc.common.enumdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RPCError {
    SERVICE_NOT_IMPLEMENT_ANY_METHODS("服务没有实现任何接口"),
    SERVICE_NOT_FOUND("服务未发现");

    private final String message;
}
