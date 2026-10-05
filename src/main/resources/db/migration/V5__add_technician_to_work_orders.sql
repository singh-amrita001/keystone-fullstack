ALTER TABLE work_orders
ADD COLUMN technician_id BIGINT NULL;

ALTER TABLE work_orders
ADD CONSTRAINT fk_work_order_technician
FOREIGN KEY (technician_id)
REFERENCES users(id);