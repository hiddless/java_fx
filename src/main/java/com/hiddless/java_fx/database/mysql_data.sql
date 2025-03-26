
CREATE DATABASE user_management;


USE user_management;


CREATE TABLE  IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- ###################################################################################################################

-- Insert
INSERT INTO users(username,password,email) VALUES ("Hiddles","323123","hiddles@gmail.com");

-- Select
select * FROM users;

-- Find User
SELECT  *  FROM users WHERE username="Hiddles" AND "323123";

-- Update
UPDATE users SET username="Hiddles81", password="3214426", email="hiddles81@gmail.com" WHERE id=1;

-- delete
DELETE FROM users  WHERE id=1;
