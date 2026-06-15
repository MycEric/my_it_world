package com.myitworld.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关白名单配置
 * <p>
 * 白名单中的路径无需 JWT 即可访问，如登录、注册接口。
 * 路径支持 Ant 风格通配符，如 /actuator/**。
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class GatewayWhitelistProperties {

    /** 免鉴权路径列表 */
    private List<String> whitelist = new ArrayList<>();
}
