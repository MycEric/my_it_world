package com.myitworld.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogArticleBrief {

    private Long id;
    private String title;
    private String summary;
    private LocalDateTime publishTime;
}
