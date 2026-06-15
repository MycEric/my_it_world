package com.myitworld.common.constant;

/**
 * 系统常量
 * <p>
 * 集中管理 JWT、Redis Key 前缀等跨服务共享的常量，避免魔法字符串散落各处。
 * </p>
 */
public final class AuthConstants {

    private AuthConstants() {
        // 工具类禁止实例化
    }

    /** HTTP 请求头中携带 Token 的字段名 */
    public static final String TOKEN_HEADER = "Authorization";

    /** Token 前缀，格式：Bearer {token} */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** JWT 载荷中的用户 ID 字段名 */
    public static final String CLAIM_USER_ID = "userId";

    /** JWT 载荷中的用户名 字段名 */
    public static final String CLAIM_USERNAME = "username";

    /** JWT 载荷中的角色列表字段名 */
    public static final String CLAIM_ROLES = "roles";

    /** JWT 载荷中的 Token 类型字段名（access / refresh） */
    public static final String CLAIM_TOKEN_TYPE = "tokenType";

    /** Access Token 类型标识 */
    public static final String TOKEN_TYPE_ACCESS = "access";

    /** Refresh Token 类型标识 */
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    /** Redis 中 Refresh Token 的 Key 前缀，完整 Key：auth:refresh:{userId}:{tokenId} */
    public static final String REDIS_REFRESH_PREFIX = "auth:refresh:";

    /** Redis 中 Access Token 黑名单 Key 前缀，完整 Key：auth:blacklist:{jti} */
    public static final String REDIS_BLACKLIST_PREFIX = "auth:blacklist:";

    /** 网关转发时传递给下游服务的用户 ID 请求头 */
    public static final String HEADER_USER_ID = "X-User-Id";

    /** 网关转发时传递给下游服务的用户名请求头 */
    public static final String HEADER_USERNAME = "X-Username";

    /** 网关转发时传递给下游服务的角色请求头 */
    public static final String HEADER_ROLES = "X-Roles";

    /** 角色：管理员 */
    public static final String ROLE_ADMIN = "ADMIN";

    /** 角色：普通用户 */
    public static final String ROLE_USER = "USER";
}
