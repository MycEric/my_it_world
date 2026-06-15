package com.myitworld.auth.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.myitworld.auth.entity.SysRole;
import com.myitworld.auth.entity.SysUser;
import com.myitworld.auth.entity.SysUserRole;
import com.myitworld.auth.mapper.SysRoleMapper;
import com.myitworld.auth.mapper.SysUserMapper;
import com.myitworld.auth.mapper.SysUserRoleMapper;
import com.myitworld.common.constant.AuthConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动数据初始化器
 * <p>
 * 首次启动时自动创建预置角色（ADMIN、USER）和管理员账号（admin/admin123），
 * 避免手动执行 SQL 时 BCrypt 哈希不一致导致无法登录。
 * 若数据已存在则跳过，保证幂等。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initAdminUser();
    }

    /**
     * 初始化系统角色
     */
    private void initRoles() {
        createRoleIfAbsent(AuthConstants.ROLE_ADMIN, "管理员", "系统管理员");
        createRoleIfAbsent(AuthConstants.ROLE_USER, "普通用户", "注册用户");
    }

    private void createRoleIfAbsent(String roleCode, String roleName, String description) {
        Long count = roleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
        if (count == 0) {
            SysRole role = new SysRole();
            role.setRoleCode(roleCode);
            role.setRoleName(roleName);
            role.setDescription(description);
            roleMapper.insert(role);
            log.info("初始化角色: {}", roleCode);
        }
    }

    /**
     * 初始化管理员账号 admin / admin123
     */
    private void initAdminUser() {
        SysUser existing = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, "admin"));
        if (existing != null) {
            return;
        }

        SysUser admin = new SysUser();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@myitworld.com");
        admin.setStatus(1);
        userMapper.insert(admin);

        bindRole(admin.getId(), AuthConstants.ROLE_ADMIN);
        bindRole(admin.getId(), AuthConstants.ROLE_USER);

        log.info("初始化管理员账号: admin / admin123");
    }

    private void bindRole(Long userId, String roleCode) {
        SysRole role = roleMapper.selectOne(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
        if (role == null) {
            return;
        }
        SysUserRole relation = new SysUserRole();
        relation.setUserId(userId);
        relation.setRoleId(role.getId());
        userRoleMapper.insert(relation);
    }
}
