package com.myitworld.auth.controller;

import com.myitworld.auth.dto.request.LoginRequest;
import com.myitworld.auth.dto.request.RefreshTokenRequest;
import com.myitworld.auth.dto.request.RegisterRequest;
import com.myitworld.auth.dto.response.TokenResponse;
import com.myitworld.auth.dto.response.UserInfoResponse;
import com.myitworld.auth.service.AuthService;
import com.myitworld.common.constant.AuthConstants;
import com.myitworld.common.exception.BusinessException;
import com.myitworld.common.result.Result;
import com.myitworld.common.result.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口控制器
 * <p>
 * 所有接口通过网关暴露，外部路径前缀为 /api/auth/**。
 * 注册、登录、刷新 Token 为公开接口；me、logout 需要 JWT 鉴权（由网关校验）。
 * </p>
 * <p>
 * 本地直连文档：http://localhost:8081/doc.html
 * 经网关访问：http://localhost:8080/api/auth/...
 * </p>
 */
@Tag(name = "认证管理", description = "注册、登录、Token 刷新、登出")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     * <p>公开接口，无需 Token。注册成功后直接返回 Token。</p>
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    /**
     * 用户登录
     * <p>公开接口，验证用户名密码后返回 Token 对。</p>
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request,
                                       HttpServletRequest httpRequest) {
        return Result.success(authService.login(request, httpRequest));
    }

    /**
     * 刷新 Token
     * <p>公开接口，使用 Refresh Token 换取新的 Access Token。</p>
     */
    @Operation(summary = "刷新 Token")
    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return Result.success(authService.refreshToken(request));
    }

    /**
     * 用户登出
     * <p>需鉴权。将当前 Access Token 加入黑名单。</p>
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = AuthConstants.TOKEN_HEADER, required = false) String authHeader) {
        authService.logout(authHeader);
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 需鉴权。用户 ID 由网关在 JWT 校验通过后通过 X-User-Id 请求头传递。
     * </p>
     */
    @Operation(summary = "获取当前用户")
    @GetMapping("/me")
    public Result<UserInfoResponse> me(
            @RequestHeader(value = AuthConstants.HEADER_USER_ID, required = false) String userIdHeader) {
        // 用户 ID 由网关在 JWT 校验通过后注入，直连 auth-service 时无此头则拒绝
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return Result.success(authService.getCurrentUser(Long.parseLong(userIdHeader)));
    }
}
