ALTER TABLE users
ADD COLUMN customer_id BIGINT NULL;

ALTER TABLE users
ADD CONSTRAINT fk_users_customer
FOREIGN KEY (customer_id)
REFERENCES customers(id);

