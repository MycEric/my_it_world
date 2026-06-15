-- 博客模块增量脚本（已有 myit_world 库时单独执行）
USE myit_world;

CREATE TABLE IF NOT EXISTS blog_category (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(64)  NOT NULL,
    slug        VARCHAR(64)  NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_slug (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS blog_article (
    id           BIGINT        NOT NULL AUTO_INCREMENT,
    title        VARCHAR(200)  NOT NULL,
    summary      VARCHAR(500)           DEFAULT NULL,
    content      LONGTEXT      NOT NULL,
    cover        VARCHAR(512)           DEFAULT NULL,
    source       VARCHAR(32)   NOT NULL DEFAULT 'LOCAL',
    source_id    VARCHAR(64)            DEFAULT NULL,
    source_url   VARCHAR(512)           DEFAULT NULL,
    status       TINYINT       NOT NULL DEFAULT 0,
    publish_time DATETIME               DEFAULT NULL,
    view_count   INT           NOT NULL DEFAULT 0,
    author_id    BIGINT                 DEFAULT NULL,
    author_name  VARCHAR(64)            DEFAULT NULL,
    deleted      TINYINT       NOT NULL DEFAULT 0,
    create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_status_publish (status, publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS blog_article_category (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    article_id  BIGINT   NOT NULL,
    category_id BIGINT   NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_category (article_id, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
