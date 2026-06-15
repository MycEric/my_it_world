-- ============================================================
-- My IT World 数据库初始化脚本
-- 阶段一：认证模块相关表 + 预置角色与管理员账号
-- 数据库：myit_world（阶段一合并单库，后续可按服务拆分）
-- ============================================================

CREATE DATABASE IF NOT EXISTS myit_world DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE myit_world;

-- ------------------------------------------------------------
-- 系统用户表：存储登录账号
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(32)  NOT NULL COMMENT '登录用户名',
    password    VARCHAR(128) NOT NULL COMMENT 'BCrypt 加密密码',
    email       VARCHAR(128)          DEFAULT NULL COMMENT '邮箱',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ------------------------------------------------------------
-- 系统角色表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    role_code   VARCHAR(32)  NOT NULL COMMENT '角色编码，如 ADMIN、USER',
    role_name   VARCHAR(64)  NOT NULL COMMENT '角色名称',
    description VARCHAR(256)          DEFAULT NULL COMMENT '描述',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ------------------------------------------------------------
-- 用户角色关联表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT   NOT NULL COMMENT '用户 ID',
    role_id     BIGINT   NOT NULL COMMENT '角色 ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id),
    KEY idx_user_id (user_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ------------------------------------------------------------
-- 登录日志表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_login_log (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT                DEFAULT NULL COMMENT '用户 ID',
    username    VARCHAR(32)  NOT NULL COMMENT '登录用户名',
    ip          VARCHAR(64)           DEFAULT NULL COMMENT '登录 IP',
    status      TINYINT      NOT NULL COMMENT '0-失败 1-成功',
    message     VARCHAR(256)          DEFAULT NULL COMMENT '备注',
    user_agent  VARCHAR(512)          DEFAULT NULL COMMENT '浏览器 UA',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 预置角色：管理员、普通用户
INSERT INTO sys_role (role_code, role_name, description) VALUES
('ADMIN', '管理员', '系统管理员，拥有后台全部权限'),
('USER',  '普通用户', '注册用户，可收藏评论等（二期扩展）');

-- 管理员账号 admin / admin123 由 auth-service 启动时 DataInitializer 自动创建（BCrypt 加密）

-- ============================================================
-- 博客模块表（模式 A：content 存 Markdown 原文）
-- ============================================================

CREATE TABLE IF NOT EXISTS blog_category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(64)  NOT NULL COMMENT '分类名称',
    slug        VARCHAR(64)  NOT NULL COMMENT 'URL 标识',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客分类';

CREATE TABLE IF NOT EXISTS blog_article (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    title        VARCHAR(200)  NOT NULL COMMENT '标题',
    summary      VARCHAR(500)           DEFAULT NULL COMMENT '摘要',
    content      LONGTEXT      NOT NULL COMMENT 'Markdown 正文原文',
    cover        VARCHAR(512)           DEFAULT NULL COMMENT '封面图 URL',
    source       VARCHAR(32)   NOT NULL DEFAULT 'LOCAL' COMMENT '来源 LOCAL/CSDN',
    source_id    VARCHAR(64)            DEFAULT NULL COMMENT '外部来源 ID',
    source_url   VARCHAR(512)           DEFAULT NULL COMMENT '原文链接',
    status       TINYINT       NOT NULL DEFAULT 0 COMMENT '0草稿 1已发布 2已下架',
    publish_time DATETIME               DEFAULT NULL COMMENT '发布时间',
    view_count   INT           NOT NULL DEFAULT 0 COMMENT '浏览量',
    author_id    BIGINT                 DEFAULT NULL COMMENT '作者 ID',
    author_name  VARCHAR(64)            DEFAULT NULL COMMENT '作者名',
    deleted      TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_status_publish (status, publish_time),
    KEY idx_source (source, source_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客文章';

CREATE TABLE IF NOT EXISTS blog_article_category (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    article_id  BIGINT   NOT NULL,
    category_id BIGINT   NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_category (article_id, category_id),
    KEY idx_article_id (article_id),
    KEY idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类关联';
