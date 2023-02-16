package yangxcc.netty.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import yangxcc.common.RPCRequest;
import yangxcc.common.RPCResponse;
import yangxcc.common.enumdata.SerializeCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoSerializer implements CommonSerializer {

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RPCResponse.class);
        kryo.register(RPCRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object o) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        Kryo kryo = kryoThreadLocal.get();
        kryo.writeObject(output, o);
        kryoThreadLocal.remove();

        return output.toBytes();
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        Kryo kryo = kryoThreadLocal.get();
        Object o = kryo.readObject(input, clazz);
        kryoThreadLocal.remove();

        return o;
    }

    @Override
    public int getCode() {
        return SerializeCode.SERIALIZE_BY_KRYO.getCode();
    }
}
