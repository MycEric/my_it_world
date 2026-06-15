-- ============================================================
-- 内容展示模块表（content-service 阶段二）
-- 数据库：myit_world
-- ============================================================

USE myit_world;

-- ------------------------------------------------------------
-- 关于我（单行配置，id 固定为 1）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS about_info (
    id          BIGINT       NOT NULL PRIMARY KEY COMMENT '固定为 1',
    slogan      VARCHAR(256)          DEFAULT NULL COMMENT '一句话 Slogan',
    summary     VARCHAR(512)          DEFAULT NULL COMMENT '简短简介',
    content     TEXT                  DEFAULT NULL COMMENT '详细介绍 Markdown',
    avatar_url  VARCHAR(512)          DEFAULT NULL COMMENT '头像 URL',
    email       VARCHAR(128)          DEFAULT NULL COMMENT '联系邮箱',
    location    VARCHAR(128)          DEFAULT NULL COMMENT '所在地',
    github_url  VARCHAR(512)          DEFAULT NULL COMMENT 'GitHub',
    csdn_url    VARCHAR(512)          DEFAULT NULL COMMENT 'CSDN',
    linkedin_url VARCHAR(512)         DEFAULT NULL COMMENT 'LinkedIn 等',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关于我';

-- ------------------------------------------------------------
-- 技能分类
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS skill_category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(64)  NOT NULL COMMENT '分类名',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能分类';

-- ------------------------------------------------------------
-- 技能项
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS skill_item (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id BIGINT       NOT NULL COMMENT '分类 ID',
    name        VARCHAR(64)  NOT NULL COMMENT '技能名称',
    level       TINYINT      NOT NULL DEFAULT 3 COMMENT '熟练度 1-5',
    icon_url    VARCHAR(512)          DEFAULT NULL COMMENT '图标 URL',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    featured    TINYINT      NOT NULL DEFAULT 0 COMMENT '是否首页精选 0否 1是',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能项';

-- ------------------------------------------------------------
-- 项目作品
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS project (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(128) NOT NULL COMMENT '项目名称',
    description TEXT                  DEFAULT NULL COMMENT '项目描述',
    cover_url   VARCHAR(512)          DEFAULT NULL COMMENT '封面图',
    github_url  VARCHAR(512)          DEFAULT NULL COMMENT 'GitHub',
    demo_url    VARCHAR(512)          DEFAULT NULL COMMENT '演示地址',
    featured    TINYINT      NOT NULL DEFAULT 0 COMMENT '精选 0否 1是',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '0下架 1展示',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目作品';

-- ------------------------------------------------------------
-- 项目技术栈
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS project_tech_stack (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    project_id  BIGINT       NOT NULL COMMENT '项目 ID',
    tech_name   VARCHAR(64)  NOT NULL COMMENT '技术名称',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序',
    PRIMARY KEY (id),
    KEY idx_project (project_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目技术栈';
