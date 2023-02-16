package yangxcc.netty.serializer;

import static yangxcc.common.enumdata.SerializeCode.SERIALIZE_BY_JSON;

public interface CommonSerializer {
    byte[] serialize(Object o);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getSerializerByCode(int code) {
        if (code == SERIALIZE_BY_JSON.getCode()) {
            return new JSONSerializer();
        } else {
            return null;
        }
    }

}
