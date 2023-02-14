package yangxcc.common;

import lombok.Data;

import yangxcc.common.enumdata.ResponseCode;

import java.io.Serializable;

@Data
public class RPCResponse<T> implements Serializable {
    /**
     * 响应状态码
     * */
    private Integer statusCode;
    /**
     * 响应信息
     * */
    private String Msg;
    /**
     * 返回数据
     * */
    private T Data;

    /**
     * 快速响应成功
     * */
    public static <T> RPCResponse <T> success(T data) {
        RPCResponse<T> response = new RPCResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMsg(ResponseCode.SUCCESS.getMsg());
        response.setData(data);
        return response;
    }

    /**
     * 快速响应失败
     * */
    public static <T> RPCResponse <T> fail() {
        RPCResponse<T> response = new RPCResponse<>();
        response.setStatusCode(ResponseCode.FAIL.getCode());
        response.setMsg(ResponseCode.FAIL.getMsg());
        return response;
    }

}
