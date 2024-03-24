CREATE TABLE flower_shop_products IF NOT EXISTS;

CREATE TABLE flower_shop_products (
    id serial PRIMARY KEY,
    category VARCHAR(50),
    name VARCHAR(50) NOT NULL,
    description TEXT,
    price integer NOT NULL check(price > 0),
    purchase_price integer check(price > 0)
);