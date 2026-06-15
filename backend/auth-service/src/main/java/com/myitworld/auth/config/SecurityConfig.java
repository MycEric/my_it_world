package com.myitworld.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全相关 Bean 配置
 * <p>
 * 阶段一 auth-service 不使用 Spring Security 完整过滤器链，
 * 仅引入 BCryptPasswordEncoder 用于密码哈希存储。
 * 网关层负责 JWT 校验，auth-service 负责签发 Token。
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * BCrypt 密码编码器
     * <p>
     * BCrypt 自带随机盐，同一明文每次加密结果不同，适合密码存储。
     * </p>
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
