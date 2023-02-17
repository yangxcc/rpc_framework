package yangxcc.common.enumdata;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NacosInfo {
    NACOS_ADDRESS("192.168.95.132", 8848);

    private final String host;
    private final int port;
}
