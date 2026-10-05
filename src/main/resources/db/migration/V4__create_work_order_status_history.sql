CREATE TABLE work_order_status_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    work_order_id BIGINT NOT NULL,
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    changed_by VARCHAR(255) NOT NULL,
    changed_at DATETIME NOT NULL,
    note VARCHAR(1000),

    PRIMARY KEY (id),

    CONSTRAINT fk_work_order_status_history_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id)
);