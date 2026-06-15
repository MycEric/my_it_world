package com.myitworld.content.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AboutSaveRequest {

    @Size(max = 256)
    private String slogan;

    @Size(max = 512)
    private String summary;

    private String content;

    @Size(max = 512)
    private String avatarUrl;

    @Size(max = 128)
    private String email;

    @Size(max = 128)
    private String location;

    @Size(max = 512)
    private String githubUrl;

    @Size(max = 512)
    private String csdnUrl;

    @Size(max = 512)
    private String linkedinUrl;
}
