package com.myitworld.common.result;

import lombok.Getter;

/**
 * 统一错误码枚举
 * <p>
 * 错误码分段规则（与企划书一致）：
 * - 200：成功
 * - 400xx：参数/业务校验错误
 * - 401xx：认证失败
 * - 403xx：权限不足
 * - 404xx：资源不存在
 * - 500xx：服务器内部错误
 * </p>
 */
@Getter
public enum ResultCode {

    /** 操作成功 */
    SUCCESS(200, "success"),

    /** 请求参数不合法 */
    BAD_REQUEST(40001, "请求参数不合法"),

    /** 用户名或密码错误（不暴露具体是用户名还是密码错误，防止枚举攻击） */
    LOGIN_FAILED(40101, "用户名或密码错误"),

    /** Token 无效或已过期 */
    TOKEN_INVALID(40102, "Token 无效或已过期"),

    /** 未登录或 Token 缺失 */
    UNAUTHORIZED(40103, "未登录，请先登录"),

    /** 无访问权限 */
    FORBIDDEN(40301, "无访问权限"),

    /** 资源不存在 */
    NOT_FOUND(40401, "资源不存在"),

    /** 用户名已存在 */
    USER_EXISTS(40002, "用户名已存在"),

    /** 邮箱已被注册 */
    EMAIL_EXISTS(40003, "邮箱已被注册"),

    /** 服务器内部错误 */
    INTERNAL_ERROR(50001, "服务器内部错误");

    /** 错误码数值 */
    private final int code;

    /** 默认错误描述 */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
