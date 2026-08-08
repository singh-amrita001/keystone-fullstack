CREATE TABLE work_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_order_code VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255),
    description VARCHAR(1000),
    priority VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    sla_due_date DATETIME,
    created_at DATETIME NOT NULL
);