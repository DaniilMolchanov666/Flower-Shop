CREATE TABLE flower_shop_products
(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category_of_product VARCHAR(50),
    name TEXT,
    description TEXT,
    price VARCHAR(50),
    purchase_price VARCHAR(50),
    name_of_photo TEXT
);

INSERT INTO flower_shop_products(category_of_product, name, description, price, purchase_price, name_of_photo) VALUES
('Цветы', 'Цветы', 'gdwjdgwjugdiy', '1', '2', 'без фото.jpg'),
('Цветы2', 'Монобукет', 'gdwjdgwjugdiy', '1', '2', 'без фото.jpg');