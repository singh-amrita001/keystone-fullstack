-- KEYSTONE: safely align users table columns.
-- Existing columns and existing user data are preserved.

SET @name_missing = (
    SELECT COUNT(*) = 0
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'users'
      AND COLUMN_NAME = 'name'
);

SET @ddl = IF(
    @name_missing,
    'ALTER TABLE users ADD COLUMN name VARCHAR(255) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE users
SET name = LEFT(
    COALESCE(
        NULLIF(TRIM(name), ''),
        NULLIF(SUBSTRING_INDEX(email, '@', 1), ''),
        CONCAT('User ', id)
    ),
    255
)
WHERE name IS NULL OR TRIM(name) = '';

SET @ddl = IF(
    @name_missing,
    'ALTER TABLE users MODIFY COLUMN name VARCHAR(255) NOT NULL',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'users'
       AND COLUMN_NAME = 'profile_pic') = 0,
    'ALTER TABLE users ADD COLUMN profile_pic VARCHAR(255) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'users'
       AND COLUMN_NAME = 'email_notifications') = 0,
    'ALTER TABLE users ADD COLUMN email_notifications TINYINT(1) NOT NULL DEFAULT 1',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'users'
       AND COLUMN_NAME = 'system_notifications') = 0,
    'ALTER TABLE users ADD COLUMN system_notifications TINYINT(1) NOT NULL DEFAULT 1',
    'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
