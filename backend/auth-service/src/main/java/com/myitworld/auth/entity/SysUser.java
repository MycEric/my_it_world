package com.myitworld.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体
 * <p>
 * 对应数据库表 sys_user，存储登录账号信息。
 * 密码字段存储 BCrypt 哈希值，禁止明文存储。
 * </p>
 */
@Data
@TableName("sys_user")
public class SysUser {

    /** 主键 ID，自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录用户名，唯一 */
    private String username;

    /** 登录密码（BCrypt 加密后的哈希值） */
    private String password;

    /** 邮箱，可选，用于找回密码等扩展功能 */
    private String email;

    /** 账号状态：0-禁用，1-正常 */
    private Integer status;

    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;

    /** 创建时间，插入时自动填充 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间，插入和更新时自动填充 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
