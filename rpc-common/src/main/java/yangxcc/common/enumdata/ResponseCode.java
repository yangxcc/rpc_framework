package yangxcc.common.enumdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(200, "调用方法成功"),
    FAIL(0, "调用方法失败");

    private final int code;
    private final String msg;
}
