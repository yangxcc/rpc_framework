package yangxcc.netty.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import yangxcc.common.RPCRequest;

import java.io.IOException;

import static yangxcc.common.enumdata.SerializeCode.SERIALIZE_BY_JSON;

@Slf4j
public class JSONSerializer implements CommonSerializer {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object o) {
        try {
            return objectMapper.writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            log.error("序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    /**
     * @param clazz 代表的是valueType，想要反序列化成什么类型的数据
     */
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RPCRequest) {
                obj = handleRequest(obj);
            }

            return obj;
        } catch (IOException e) {
            log.error("反序列化时有错误发生：{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getCode() {
        return SERIALIZE_BY_JSON.getCode();
    }

    /**
     * 这里使用了json序列化和反序列化Object数组，无法保证反序列化后仍为原类型
     * @param obj
     * @return
     */
    @SneakyThrows
    private Object handleRequest(Object obj) {
        RPCRequest request = (RPCRequest) obj;
        Class<?>[] parameterTypes = request.getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            // 数据类型发生了改变，比如int类型的1变成了“1”
            if (!parameterTypes[i].isAssignableFrom(request.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(request.getParameters()[i]);
                request.getParameters()[i] = objectMapper.readValue(bytes, parameterTypes[i]);
            }
        }

        return request;
    }
}
