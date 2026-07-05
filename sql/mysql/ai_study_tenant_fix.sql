SET NAMES utf8mb4;

DROP PROCEDURE IF EXISTS add_ai_tenant_column;

DELIMITER //
CREATE PROCEDURE add_ai_tenant_column(IN tableName VARCHAR(64))
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tableName
      AND COLUMN_NAME = 'tenant_id'
  ) THEN
    SET @sql = CONCAT('ALTER TABLE `', tableName, '` ADD COLUMN `tenant_id` BIGINT NOT NULL DEFAULT 1 COMMENT ''tenant id'' AFTER `id`');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END//
DELIMITER ;

CALL add_ai_tenant_column('ai_skill');
CALL add_ai_tenant_column('ai_skill_progress');
CALL add_ai_tenant_column('ai_learning_record');
CALL add_ai_tenant_column('ai_chat_record');
CALL add_ai_tenant_column('ai_resume');
CALL add_ai_tenant_column('ai_knowledge_point');
CALL add_ai_tenant_column('ai_learning_event');
CALL add_ai_tenant_column('ai_user_learning_profile');
CALL add_ai_tenant_column('ai_tutor_memory');

DROP PROCEDURE IF EXISTS add_ai_tenant_column;

DROP PROCEDURE IF EXISTS add_ai_column;

DELIMITER //
CREATE PROCEDURE add_ai_column(IN tableName VARCHAR(64), IN columnName VARCHAR(64), IN ddlText VARCHAR(512))
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = tableName
      AND COLUMN_NAME = columnName
  ) THEN
    SET @sql = CONCAT('ALTER TABLE `', tableName, '` ADD COLUMN ', ddlText);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END//
DELIMITER ;

CALL add_ai_column('ai_chat_record', 'dify_message_id', '`dify_message_id` VARCHAR(128) NULL COMMENT ''Dify message id'' AFTER `conversation_id`');
CALL add_ai_column('ai_chat_record', 'raw_response', '`raw_response` LONGTEXT NULL COMMENT ''Dify raw response'' AFTER `dify_message_id`');
CALL add_ai_column('ai_chat_record', 'parsed_response', '`parsed_response` LONGTEXT NULL COMMENT ''Parsed AI response'' AFTER `raw_response`');

DROP PROCEDURE IF EXISTS add_ai_column;
