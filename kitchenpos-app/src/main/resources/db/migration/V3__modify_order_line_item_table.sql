ALTER TABLE order_line_item DROP COLUMN menu_id;
ALTER TABLE order_line_item ADD COLUMN name varchar(255) not null;
ALTER TABLE order_line_item ADD COLUMN price bigint not null
