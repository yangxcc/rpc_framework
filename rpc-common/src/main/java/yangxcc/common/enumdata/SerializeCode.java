package yangxcc.common.enumdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SerializeCode {
    SERIALIZE_BY_KRYO(0),
    SERIALIZE_BY_JSON(1);

    private final int code;
}
