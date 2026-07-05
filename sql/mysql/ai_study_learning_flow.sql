SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS ai_knowledge_point (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT 'tenant id',
    user_id BIGINT NOT NULL COMMENT 'user id',
    skill_id BIGINT NOT NULL COMMENT 'skill id',
    content VARCHAR(500) NOT NULL COMMENT 'knowledge point',
    source_chat_id BIGINT NULL COMMENT 'source chat id',
    stage TINYINT NOT NULL DEFAULT 0 COMMENT 'review stage',
    next_review_time DATETIME NULL COMMENT 'next review time',
    last_review_time DATETIME NULL COMMENT 'last review time',
    taught_by VARCHAR(64) NULL COMMENT 'tutor code',
    batch_id VARCHAR(64) NULL COMMENT 'batch id',
    creator VARCHAR(64) DEFAULT '',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    INDEX idx_user_skill (user_id, skill_id),
    INDEX idx_next_review (user_id, next_review_time),
    INDEX idx_source_chat (source_chat_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI study knowledge point';

CREATE TABLE IF NOT EXISTS ai_learning_event (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT 'tenant id',
    user_id BIGINT NOT NULL COMMENT 'user id',
    skill_id BIGINT NULL COMMENT 'skill id',
    event_type VARCHAR(32) NOT NULL COMMENT 'event type',
    ref_id BIGINT NULL COMMENT 'reference id',
    score_delta INT NOT NULL DEFAULT 0 COMMENT 'xp delta',
    meta JSON NULL COMMENT 'event metadata',
    creator VARCHAR(64) DEFAULT '',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    INDEX idx_user_time (user_id, create_time),
    INDEX idx_user_skill (user_id, skill_id),
    INDEX idx_event_type (event_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI study learning event';

CREATE TABLE IF NOT EXISTS ai_user_learning_profile (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT 'tenant id',
    user_id BIGINT NOT NULL COMMENT 'user id',
    total_xp INT NOT NULL DEFAULT 0 COMMENT 'total xp',
    level_no INT NOT NULL DEFAULT 1 COMMENT 'level number',
    current_streak INT NOT NULL DEFAULT 0 COMMENT 'current streak days',
    longest_streak INT NOT NULL DEFAULT 0 COMMENT 'longest streak days',
    last_active_date DATE NULL COMMENT 'last active date',
    weekly_xp JSON NULL COMMENT 'recent xp by date',
    creator VARCHAR(64) DEFAULT '',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI study user learning profile';

CREATE TABLE IF NOT EXISTS ai_tutor_memory (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'primary key',
    tenant_id BIGINT NOT NULL DEFAULT 1 COMMENT 'tenant id',
    user_id BIGINT NOT NULL COMMENT 'user id',
    skill_id BIGINT NULL COMMENT 'skill id',
    tutor_code VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT 'tutor code',
    taught_summary TEXT NULL COMMENT 'taught summary',
    stuck_points TEXT NULL COMMENT 'stuck points',
    next_suggestion TEXT NULL COMMENT 'next suggestion',
    attitude VARCHAR(64) NULL COMMENT 'learning attitude',
    creator VARCHAR(64) DEFAULT '',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_skill_tutor (user_id, skill_id, tutor_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI study tutor memory';

DROP PROCEDURE IF EXISTS add_ai_learning_flow_column;
DELIMITER //
CREATE PROCEDURE add_ai_learning_flow_column(
    IN table_name_value VARCHAR(64),
    IN column_name_value VARCHAR(64),
    IN column_sql_value TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = table_name_value
          AND COLUMN_NAME = column_name_value
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `', table_name_value, '` ADD COLUMN ', column_sql_value);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //
DELIMITER ;

CALL add_ai_learning_flow_column('ai_knowledge_point', 'tenant_id', '`tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT ''tenant id'' AFTER `id`');
CALL add_ai_learning_flow_column('ai_learning_event', 'tenant_id', '`tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT ''tenant id'' AFTER `id`');
CALL add_ai_learning_flow_column('ai_user_learning_profile', 'tenant_id', '`tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT ''tenant id'' AFTER `id`');
CALL add_ai_learning_flow_column('ai_tutor_memory', 'tenant_id', '`tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT ''tenant id'' AFTER `id`');

DROP PROCEDURE IF EXISTS add_ai_learning_flow_column;
