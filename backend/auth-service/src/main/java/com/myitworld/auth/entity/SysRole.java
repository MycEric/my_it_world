package com.myitworld.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统角色实体
 * <p>
 * 对应数据库表 sys_role，预置 ADMIN（管理员）和 USER（普通用户）两种角色。
 * </p>
 */
@Data
@TableName("sys_role")
public class SysRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编码，如 ADMIN、USER，用于 JWT 与权限判断 */
    private String roleCode;

    /** 角色名称，用于展示 */
    private String roleName;

    /** 角色描述 */
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
