package yangxcc.rpc.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor // 生成全参数构造器
public class HelloObject implements Serializable { // 这里要是先Serialized类，实现序列化
    private int id;
    private String message;
}
