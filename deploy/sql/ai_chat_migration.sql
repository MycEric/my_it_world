-- ============================================================
-- AI 对话会话与消息表（阶段 2.1 会话历史一期）
-- 数据库：myit_world
-- ============================================================

USE myit_world;

-- ------------------------------------------------------------
-- AI 对话会话表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ai_chat_session (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    session_id  VARCHAR(64)  NOT NULL COMMENT '会话 UUID',
    user_id     BIGINT                DEFAULT NULL COMMENT '登录用户 ID；游客为 NULL',
    title       VARCHAR(200)          DEFAULT NULL COMMENT '会话标题（首条问题摘要）',
    model       VARCHAR(64)  NOT NULL DEFAULT 'qwen-turbo' COMMENT '使用的模型',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删 1-已删',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_session_id (session_id),
    KEY idx_user_updated (user_id, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 对话会话表';

-- ------------------------------------------------------------
-- AI 对话消息表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ai_chat_message (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    session_id   VARCHAR(64)  NOT NULL COMMENT '会话 UUID',
    role         VARCHAR(16)  NOT NULL COMMENT 'user / assistant / system',
    content      TEXT         NOT NULL COMMENT '消息内容',
    token_count  INT                   DEFAULT NULL COMMENT 'Token 数（可选）',
    sources_json JSON                  DEFAULT NULL COMMENT 'RAG 引用来源（二期）',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_session_time (session_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 对话消息表';
