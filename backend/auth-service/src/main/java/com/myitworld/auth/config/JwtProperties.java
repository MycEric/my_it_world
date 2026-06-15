package com.myitworld.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 * <p>
 * 从 application.yml 的 jwt.* 节点读取配置，
 * 供 AuthService、JwtUtil 等组件注入使用。
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** JWT 签名密钥，必须与 gateway-service 保持一致 */
    private String secret;

    /** Access Token 过期时间（毫秒） */
    private long accessExpiration;

    /** Refresh Token 过期时间（毫秒） */
    private long refreshExpiration;
}
