CREATE TABLE IF NOT EXISTS customers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20),
    address VARCHAR(500),
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS sites (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(1000),
    city VARCHAR(255),
    state VARCHAR(255),
    postal_code VARCHAR(255),
    customer_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sites_customer
        FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS parts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    part_number VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    quantity_in_stock INT NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_parts_part_number (part_number)
);

CREATE TABLE IF NOT EXISTS part_usages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    work_order_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(12,2) NOT NULL,
    used_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_part_usages_work_order
        FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    CONSTRAINT fk_part_usages_part
        FOREIGN KEY (part_id) REFERENCES parts(id)
);

ALTER TABLE part_usages
ADD COLUMN total_cost DECIMAL(12,2) NOT NULL;

CREATE TABLE IF NOT EXISTS time_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    work_order_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    minutes INT NOT NULL,
    note VARCHAR(1000),
    logged_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_time_logs_work_order
        FOREIGN KEY (work_order_id) REFERENCES work_orders(id),
    CONSTRAINT fk_time_logs_technician
        FOREIGN KEY (technician_id) REFERENCES users(id)
);