ALTER TABLE work_orders
ADD COLUMN customer_id BIGINT NULL,
ADD COLUMN site_id BIGINT NULL;

UPDATE work_orders
SET customer_id = 11,
    site_id = 6
WHERE customer_id IS NULL
  AND site_id IS NULL;

ALTER TABLE work_orders
MODIFY COLUMN customer_id BIGINT NOT NULL,
MODIFY COLUMN site_id BIGINT NOT NULL;

ALTER TABLE work_orders
ADD CONSTRAINT fk_work_order_customer
FOREIGN KEY (customer_id)
REFERENCES customers(id);

ALTER TABLE work_orders
ADD CONSTRAINT fk_work_order_site
FOREIGN KEY (site_id)
REFERENCES sites(id);