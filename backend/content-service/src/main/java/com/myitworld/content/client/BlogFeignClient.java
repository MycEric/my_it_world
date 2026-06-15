package com.myitworld.content.client;

import com.myitworld.common.result.Result;
import com.myitworld.content.dto.BlogArticleBrief;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "blog-service")
public interface BlogFeignClient {

    @GetMapping("/api/blogs/latest")
    Result<List<BlogArticleBrief>> latest(@RequestParam("limit") int limit);
}
