package com.myitworld.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志实体
 * <p>
 * 记录每次登录尝试（成功/失败），便于安全审计与异常登录检测。
 * </p>
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户 ID，登录失败且用户不存在时可为 null */
    private Long userId;

    /** 尝试登录的用户名 */
    private String username;

    /** 登录 IP 地址 */
    private String ip;

    /** 登录结果：0-失败，1-成功 */
    private Integer status;

    /** 失败原因或备注 */
    private String message;

    /** 浏览器 User-Agent */
    private String userAgent;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
