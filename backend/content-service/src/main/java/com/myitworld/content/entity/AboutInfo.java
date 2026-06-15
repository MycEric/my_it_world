package com.myitworld.content.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("about_info")
public class AboutInfo {

    @TableId
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

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
