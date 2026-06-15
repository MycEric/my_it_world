package com.myitworld.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.myitworld.auth.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 Mapper
 * <p>
 * 继承 MyBatis-Plus BaseMapper 获得基础 CRUD；
 * 自定义 SQL 用于关联查询用户角色。
 * </p>
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户 ID 查询角色编码列表
     * <p>
     * 通过 sys_user_role 与 sys_role 联表查询，用于 JWT 签发时写入 roles 声明。
     * </p>
     */
    @Select("""
            SELECT r.role_code
            FROM sys_role r
            INNER JOIN sys_user_role ur ON r.id = ur.role_id
            WHERE ur.user_id = #{userId}
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
