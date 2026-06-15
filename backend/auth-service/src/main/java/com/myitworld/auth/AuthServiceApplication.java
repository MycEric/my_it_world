package com.myitworld.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 认证服务启动类
 * <p>
 * auth-service 职责：
 * 1. 用户注册与登录
 * 2. JWT Access Token / Refresh Token 签发与刷新
 * 3. 登出时将 Token 加入 Redis 黑名单
 * 4. 登录日志记录
 * </p>
 * <p>
 * 启动前请确保 Nacos、MySQL、Redis 已就绪（可通过 deploy/docker-compose.yml 启动）。
 * </p>
 */
@SpringBootApplication(scanBasePackages = {"com.myitworld.auth", "com.myitworld.common"})
@EnableDiscoveryClient
@MapperScan("com.myitworld.auth.mapper")
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
