package yangxcc.netty.serializer;

import static yangxcc.common.enumdata.SerializeCode.SERIALIZE_BY_JSON;

public interface CommonSerializer {
    byte[] serialize(Object o);

    Object deserialize(byte[] bytes, Class<?> clazz);

    int getCode();

    static CommonSerializer getSerializerByCode(int code) {
        switch (code) {
            case 1 : {
                return new JSONSerializer();
            }
            case 0 : {
                return new KryoSerializer();
            }
        }

        return null;
    }

}
