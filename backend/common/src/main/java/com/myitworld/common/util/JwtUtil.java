package com.myitworld.common.util;

import com.myitworld.common.constant.AuthConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * JWT 工具类
 * <p>
 * 负责 Access Token 与 Refresh Token 的签发、解析与校验。
 * 网关与各微服务共用同一 secret，保证 Token 可在网关层本地验签。
 * </p>
 * <p>
 * Access Token：短期有效（默认 2 小时），用于 API 鉴权。
 * Refresh Token：长期有效（默认 7 天），仅存 Redis，用于刷新 Access Token。
 * </p>
 */
public final class JwtUtil {

    private JwtUtil() {
    }

    /**
     * 根据 secret 字符串生成 HMAC-SHA 签名密钥
     */
    public static SecretKey buildKey(String secret) {
        // JJWT 要求密钥长度至少 256 位（32 字节）
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 签发 Access Token
     *
     * @param userId           用户 ID
     * @param username         用户名
     * @param roles            角色列表，如 ["ADMIN", "USER"]
     * @param secret           JWT 签名密钥
     * @param expirationMillis 过期时间（毫秒）
     * @return JWT 字符串
     */
    public static String createAccessToken(Long userId, String username, List<String> roles,
                                           String secret, long expirationMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userId))
                .claim(AuthConstants.CLAIM_USER_ID, userId)
                .claim(AuthConstants.CLAIM_USERNAME, username)
                .claim(AuthConstants.CLAIM_ROLES, roles)
                .claim(AuthConstants.CLAIM_TOKEN_TYPE, AuthConstants.TOKEN_TYPE_ACCESS)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(buildKey(secret))
                .compact();
    }

    /**
     * 签发 Refresh Token
     *
     * @param userId           用户 ID
     * @param username         用户名
     * @param secret           JWT 签名密钥
     * @param expirationMillis 过期时间（毫秒）
     * @return JWT 字符串（含 jti，用于 Redis 存储与吊销）
     */
    public static String createRefreshToken(Long userId, String username,
                                            String secret, long expirationMillis) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userId))
                .claim(AuthConstants.CLAIM_USER_ID, userId)
                .claim(AuthConstants.CLAIM_USERNAME, username)
                .claim(AuthConstants.CLAIM_TOKEN_TYPE, AuthConstants.TOKEN_TYPE_REFRESH)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(buildKey(secret))
                .compact();
    }

    /**
     * 解析 Token 并返回 Claims；签名无效或过期会抛出异常
     */
    public static Claims parseToken(String token, String secret) {
        return Jwts.parser()
                .verifyWith(buildKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 安全解析 Token：过期返回 null 而非抛异常（用于网关可选校验场景）
     */
    public static Claims parseTokenSafely(String token, String secret) {
        try {
            return parseToken(token, secret);
        } catch (ExpiredJwtException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从 Authorization 请求头中提取纯 Token（去掉 "Bearer " 前缀）
     */
    public static String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith(AuthConstants.TOKEN_PREFIX)) {
            return authHeader.substring(AuthConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 获取 Token 的唯一标识 jti（用于黑名单）
     */
    public static String getJti(Claims claims) {
        return claims.getId();
    }

    /**
     * 判断是否为 Access Token
     */
    public static boolean isAccessToken(Claims claims) {
        return AuthConstants.TOKEN_TYPE_ACCESS.equals(claims.get(AuthConstants.CLAIM_TOKEN_TYPE));
    }

    /**
     * 判断是否为 Refresh Token
     */
    public static boolean isRefreshToken(Claims claims) {
        return AuthConstants.TOKEN_TYPE_REFRESH.equals(claims.get(AuthConstants.CLAIM_TOKEN_TYPE));
    }
}
