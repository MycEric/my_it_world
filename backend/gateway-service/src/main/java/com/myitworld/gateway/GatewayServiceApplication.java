package com.myitworld.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API 网关启动类
 * <p>
 * gateway-service 职责：
 * 1. 作为所有外部请求的唯一入口（端口 8080）
 * 2. 根据路径前缀路由到对应微服务（如 /api/auth/** → auth-service）
 * 3. JWT Token 校验与白名单放行
 * 4. 跨域 CORS 处理
 * 5. 校验通过后将用户信息写入请求头传递给下游服务
 * </p>
 */
@SpringBootApplication(scanBasePackages = {"com.myitworld.gateway", "com.myitworld.common"})
@EnableDiscoveryClient
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
