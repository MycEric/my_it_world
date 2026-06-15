package com.myitworld.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 博客服务启动类
 * <p>
 * blog-service 职责（模式 A：MySQL 存 Markdown）：
 * 1. 公开接口：已发布文章列表、详情、最新文章
 * 2. 管理接口：新建/编辑/删除/发布/下架、Markdown 文件导入
 * 3. 正文以 Markdown 原文存入 blog_article.content，前端负责渲染
 * </p>
 */
@SpringBootApplication(scanBasePackages = {"com.myitworld.blog", "com.myitworld.common"})
@EnableDiscoveryClient
@MapperScan("com.myitworld.blog.mapper")
public class BlogServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogServiceApplication.class, args);
    }
}
