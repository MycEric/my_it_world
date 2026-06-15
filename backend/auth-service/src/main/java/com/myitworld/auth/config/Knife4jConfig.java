package com.myitworld.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 文档配置
 * <p>
 * 启动后访问：http://localhost:8081/doc.html
 * </p>
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI authOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("My IT World 认证服务接口文档 - 注册/登录/JWT")
                        .version("1.0.0")
                        .contact(new Contact().name("My IT World").url("https://github.com")));
    }
}
