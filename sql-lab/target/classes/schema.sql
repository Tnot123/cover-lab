DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id INT PRIMARY KEY,
  name VARCHAR(200),
  category VARCHAR(100),
  price DECIMAL(10,2)
);
