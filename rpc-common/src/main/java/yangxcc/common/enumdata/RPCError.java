package yangxcc.common.enumdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RPCError {
    SERVICE_NOT_IMPLEMENT_ANY_METHODS("服务没有实现任何接口"),
    SERVICE_NOT_FOUND("服务未发现"),

    PACKAGE_NOT_MATCH("不能够识别的数据包"),
    PACKAGE_TYPE_NOT_FOUND("不符合规则的数据包类型"),
    UNKNOWN_DECODER("不能识别的反序列化器"),

    FAIL_CONNECT_TO_NACOS("未能成功连接到nacos注册中心"),
    REGISTER_SERVICE_FAILED("注册服务失败"),
    GET_SERVICE_FROM_NACOS_FAILED("向nacos请求服务失败"),

    SERIALIZER_NOT_FOUND("序列化器未发现"),

    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务器失败");

    private final String message;
}
