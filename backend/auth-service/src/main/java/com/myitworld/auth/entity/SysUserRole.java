package com.myitworld.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户-角色关联实体
 * <p>
 * 多对多关系中间表，一个用户可拥有多个角色（如同时是 USER 和 ADMIN）。
 * </p>
 */
@Data
@TableName("sys_user_role")
public class SysUserRole {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户 ID，关联 sys_user.id */
    private Long userId;

    /** 角色 ID，关联 sys_role.id */
    private Long roleId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
