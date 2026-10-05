CREATE DATABASE keystone;

SHOW DATABASES;
USE keystone;

SHOW TABLES;

SELECT * FROM users;
SELECT * FROM work_orders;

DESCRIBE users;
DESCRIBE work_orders;

SHOW CREATE TABLE users;

SHOW CREATE TABLE work_orders;

INSERT INTO users (name, email, password, role)
VALUES (
    'New User',
    'newuser@gmail.com',
    '$2a$10$examplehash',
    'USER'
);