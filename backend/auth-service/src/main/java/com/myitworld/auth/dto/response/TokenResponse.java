package com.myitworld.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录/注册成功后的 Token 响应 DTO
 * <p>
 * 返回 Access Token 与 Refresh Token，前端分别存储：
 * - Access Token：每次 API 请求携带（内存或 sessionStorage）
 * - Refresh Token：用于静默刷新（localStorage，注意 XSS 防护）
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token 响应")
public class TokenResponse {

    @Schema(description = "Access Token，用于 API 鉴权")
    private String accessToken;

    @Schema(description = "Refresh Token，用于刷新 Access Token")
    private String refreshToken;

    @Schema(description = "Access Token 类型，固定为 Bearer")
    private String tokenType;

    @Schema(description = "Access Token 过期时间（秒）")
    private long expiresIn;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "角色列表")
    private List<String> roles;
}
