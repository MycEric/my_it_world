package com.myitworld.content.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AboutVO {

    private Long id;
    private String slogan;
    private String summary;
    private String content;
    private String avatarUrl;
    private String email;
    private String location;
    private String githubUrl;
    private String csdnUrl;
    private String linkedinUrl;
    private LocalDateTime updateTime;
}
