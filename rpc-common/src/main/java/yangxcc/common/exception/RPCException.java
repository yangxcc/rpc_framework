package yangxcc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import yangxcc.common.enumdata.RPCError;

public class RPCException extends RuntimeException {
    private RPCError error;

    public RPCException(RPCError error) {
        // 调用父类的构造方法
        super(error.getMessage());
    }
}
