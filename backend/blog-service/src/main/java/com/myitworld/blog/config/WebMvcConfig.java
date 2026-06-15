package com.myitworld.blog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC 配置：Admin 拦截器 + CSDN 导出图片静态映射
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminInterceptor adminInterceptor;
    private final BlogImportProperties importProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/blogs/admin/**");
    }

    /**
     * /api/blogs/assets/{articleId}/{file} → output/images/{articleId}/{file}
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path outputRoot = Paths.get(importProperties.getOutputDir()).normalize();
        if (!outputRoot.isAbsolute()) {
            outputRoot = Paths.get(System.getProperty("user.dir")).resolve(outputRoot).normalize();
        }
        String imagesPath = outputRoot.resolve("images").toUri().toString();
        if (!imagesPath.endsWith("/")) {
            imagesPath = imagesPath + "/";
        }
        registry.addResourceHandler(importProperties.getAssetsUrlPrefix() + "/**")
                .addResourceLocations(imagesPath);
    }
}
