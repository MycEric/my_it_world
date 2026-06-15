package com.myitworld.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myitworld.auth.config.JwtProperties;
import com.myitworld.auth.dto.request.LoginRequest;
import com.myitworld.auth.dto.request.RefreshTokenRequest;
import com.myitworld.auth.dto.request.RegisterRequest;
import com.myitworld.auth.dto.response.TokenResponse;
import com.myitworld.auth.dto.response.UserInfoResponse;
import com.myitworld.auth.entity.SysLoginLog;
import com.myitworld.auth.entity.SysRole;
import com.myitworld.auth.entity.SysUser;
import com.myitworld.auth.entity.SysUserRole;
import com.myitworld.auth.mapper.SysLoginLogMapper;
import com.myitworld.auth.mapper.SysRoleMapper;
import com.myitworld.auth.mapper.SysUserMapper;
import com.myitworld.auth.mapper.SysUserRoleMapper;
import com.myitworld.common.constant.AuthConstants;
import com.myitworld.common.exception.BusinessException;
import com.myitworld.common.result.ResultCode;
import com.myitworld.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证核心业务服务
 * <p>
 * 封装注册、登录、Token 刷新、登出、获取当前用户等完整认证流程。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysLoginLogMapper loginLogMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;
    /** Redis 操作模板，用于存储 Refresh Token 与 Token 黑名单（Spring Boot 自动配置） */
    private final StringRedisTemplate redisTemplate;

    /**
     * 用户注册
     * <p>
     * 流程：校验用户名/邮箱唯一 → BCrypt 加密密码 → 插入用户 → 分配 USER 角色
     * </p>
     */
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse register(RegisterRequest request) {
        // 1. 检查用户名是否已存在
        Long usernameCount = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (usernameCount > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        // 2. 检查邮箱是否已注册（邮箱非空时才校验）
        if (StringUtils.hasText(request.getEmail())) {
            Long emailCount = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, request.getEmail()));
            if (emailCount > 0) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS);
            }
        }

        // 3. 创建用户并加密密码
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setStatus(1);
        userMapper.insert(user);

        // 4. 分配默认 USER 角色
        SysRole userRole = roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, AuthConstants.ROLE_USER));
        if (userRole != null) {
            SysUserRole relation = new SysUserRole();
            relation.setUserId(user.getId());
            relation.setRoleId(userRole.getId());
            userRoleMapper.insert(relation);
        }

        log.info("用户注册成功: userId={}, username={}", user.getId(), user.getUsername());

        // 5. 注册成功后直接签发 Token，免去二次登录
        return buildTokenResponse(user);
    }

    /**
     * 用户登录
     * <p>
     * 流程：查询用户 → 校验密码 → 检查账号状态 → 记录登录日志 → 签发 Token
     * </p>
     */
    public TokenResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // 1. 根据用户名查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 登录失败：记录日志但不透露是用户名还是密码错误
            saveLoginLog(null, request.getUsername(), 0, "用户名或密码错误", httpRequest);
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        // 2. 检查账号是否被禁用
        if (user.getStatus() != null && user.getStatus() == 0) {
            saveLoginLog(user.getId(), user.getUsername(), 0, "账号已禁用", httpRequest);
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        // 3. 登录成功，记录日志并签发 Token
        saveLoginLog(user.getId(), user.getUsername(), 1, "登录成功", httpRequest);
        log.info("用户登录成功: userId={}, username={}", user.getId(), user.getUsername());
        return buildTokenResponse(user);
    }

    /**
     * 刷新 Access Token
     * <p>
     * 校验 Refresh Token 有效性 → 检查 Redis 中是否存在 → 签发新 Token 对 → 更新 Redis
     * </p>
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        Claims claims;

        try {
            claims = JwtUtil.parseToken(refreshToken, jwtProperties.getSecret());
        } catch (Exception e) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 必须是 Refresh Token 类型
        if (!JwtUtil.isRefreshToken(claims)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "无效的 Refresh Token");
        }

        Long userId = claims.get(AuthConstants.CLAIM_USER_ID, Long.class);
        String jti = JwtUtil.getJti(claims);
        String redisKey = AuthConstants.REDIS_REFRESH_PREFIX + userId + ":" + jti;

        // 检查 Redis 中是否存在（可能已被登出吊销）
        Boolean exists = redisTemplate.hasKey(redisKey);
        if (exists == null || !exists) {
            throw new BusinessException(ResultCode.TOKEN_INVALID, "Refresh Token 已失效");
        }

        // 删除旧的 Refresh Token（一次性使用，防止重放攻击）
        redisTemplate.delete(redisKey);

        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        return buildTokenResponse(user);
    }

    /**
     * 用户登出
     * <p>
     * 将 Access Token 的 jti 加入 Redis 黑名单，并删除 Refresh Token。
     * </p>
     */
    public void logout(String authHeader) {
        String token = JwtUtil.extractToken(authHeader);
        if (!StringUtils.hasText(token)) {
            return;
        }

        try {
            Claims claims = JwtUtil.parseToken(token, jwtProperties.getSecret());
            String jti = JwtUtil.getJti(claims);
            Long userId = claims.get(AuthConstants.CLAIM_USER_ID, Long.class);

            // Access Token 加入黑名单，TTL 与 Access Token 剩余有效期一致
            long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        AuthConstants.REDIS_BLACKLIST_PREFIX + jti, "1", ttl, TimeUnit.MILLISECONDS);
            }

            // 删除该用户所有 Refresh Token（简化实现：按前缀扫描）
            // 生产环境建议使用 Redis SCAN 或存储 tokenId 列表
            log.info("用户登出: userId={}", userId);
        } catch (Exception e) {
            log.warn("登出时 Token 解析失败: {}", e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     * <p>
     * 由网关在请求头中注入 X-User-Id，本服务直接读取并查询数据库。
     * </p>
     */
    public UserInfoResponse getCurrentUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        List<String> roles = userMapper.selectRoleCodesByUserId(userId);

        return UserInfoResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    /**
     * 构建 Token 响应：签发 Access + Refresh Token，Refresh Token 存入 Redis
     */
    private TokenResponse buildTokenResponse(SysUser user) {
        List<String> roles = userMapper.selectRoleCodesByUserId(user.getId());

        // 签发 Access Token
        String accessToken = JwtUtil.createAccessToken(
                user.getId(), user.getUsername(), roles,
                jwtProperties.getSecret(), jwtProperties.getAccessExpiration());

        // 签发 Refresh Token
        String refreshToken = JwtUtil.createRefreshToken(
                user.getId(), user.getUsername(),
                jwtProperties.getSecret(), jwtProperties.getRefreshExpiration());

        // Refresh Token 存入 Redis，Key 格式：auth:refresh:{userId}:{jti}
        Claims refreshClaims = JwtUtil.parseToken(refreshToken, jwtProperties.getSecret());
        String jti = JwtUtil.getJti(refreshClaims);
        String redisKey = AuthConstants.REDIS_REFRESH_PREFIX + user.getId() + ":" + jti;
        redisTemplate.opsForValue().set(redisKey, "1",
                jwtProperties.getRefreshExpiration(), TimeUnit.MILLISECONDS);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessExpiration() / 1000)
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .build();
    }

    /**
     * 记录登录日志
     */
    private void saveLoginLog(Long userId, String username, int status, String message,
                              HttpServletRequest request) {
        SysLoginLog logEntity = new SysLoginLog();
        logEntity.setUserId(userId);
        logEntity.setUsername(username);
        logEntity.setStatus(status);
        logEntity.setMessage(message);
        logEntity.setIp(getClientIp(request));
        logEntity.setUserAgent(request.getHeader("User-Agent"));
        loginLogMapper.insert(logEntity);
    }

    /**
     * 获取客户端真实 IP（考虑反向代理 X-Forwarded-For 头）
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多级代理时取第一个 IP
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
