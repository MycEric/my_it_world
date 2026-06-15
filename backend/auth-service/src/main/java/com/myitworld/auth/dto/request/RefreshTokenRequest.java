package com.myitworld.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Token 请求 DTO
 * <p>
 * 当 Access Token 过期时，前端携带 Refresh Token 调用 /refresh 接口获取新的 Token 对。
 * </p>
 */
@Data
@Schema(description = "刷新 Token 请求")
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh Token 不能为空")
    @Schema(description = "Refresh Token")
    private String refreshToken;
}
