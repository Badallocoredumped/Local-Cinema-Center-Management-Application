/*Create tables for Products, Tickets\monthly schedule, Movies, halls, Employees, Customers, Orders(Payments, and Payments history)*/

CREATE DATABASE Group16

CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

USE Group16;




CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('Manager', 'Admin', 'Cashier') NOT NULL,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL
);

SELECT * FROM employees;
/*We will create the database manually*/
INSERT INTO employees (username, password, role, name, surname)
VALUES 
('emir5757', 'Homelander', 'Manager', 'Emir', 'Özen'),
('Teca7', 'WeLoveTeca', 'Admin', 'Ahmed Marcolino Teca', 'Kanadji'),
('manager1', 'manager1', 'Manager', 'Mana', 'Ger'),
('admin1', 'admin1', 'Admin', 'Ad', 'Min'),
('cashier1', 'cashier1', 'Cashier', 'Cash', 'Ier');



/*Admin related tables*/
CREATE TABLE Movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    poster_url VARCHAR(255),
    poster_image LONGBLOB,
    genre VARCHAR(255), -- Comma-separated genres or normalize into a separate table
    summary TEXT,
    duration INT NOT NULL
);
SELECT * FROM Movies;
/*We will create the database manually*/
INSERT INTO Movies (title, poster_url, poster_image, genre, summary, duration)
VALUES 
('Titanic', '', '', 'Romance', 'Love in Cruiser that sinks', '160');

 -- A way to upload jps files to the database
SET SQL_SAFE_UPDATES = 0;
UPDATE Movies
SET poster_image = LOAD_FILE('C:/ProgramData/MySQL/MySQL Server 9.1/Uploads/titanic.jpeg')
WHERE title = 'Titanic';
SET SQL_SAFE_UPDATES = 1;

-- to check if the upload worked
SELECT movie_id, title, LENGTH(poster_image) AS image_size
FROM Movies
WHERE title = 'Titanic';


CREATE TABLE Halls (
    hall_id INT AUTO_INCREMENT PRIMARY KEY,
    hall_name ENUM('Hall_A', 'Hall_B') UNIQUE NOT NULL,
    capacity INT NOT NULL
);

CREATE TABLE Schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    schedule_name VARCHAR(255) NOT NULL UNIQUE,
    movie_id INT NOT NULL,
    hall_name VARCHAR(50) UNIQUE NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    vacant_seats INT NOT NULL,
    sold_ticket BOOLEAN DEFAULT FALSE, /*Checks if tickets were sold for this session*/
    FOREIGN KEY (movie_id) REFERENCES Movies(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (hall_name) REFERENCES Halls(hall_name) ON DELETE CASCADE
);

CREATE TABLE Tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    schedule_id INT NOT NULL,
    customer_name VARCHAR(100),
    age INT,
    seat_number INT NOT NULL,
    discount_applied BOOLEAN DEFAULT FALSE,
    refunded BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (schedule_id) REFERENCES Schedules(schedule_id) ON DELETE CASCADE,
);

CREATE TABLE ProductSales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    is_refunded BOOLEAN DEFAULT FALSE,
    refunded_quantity INT DEFAULT,
    FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE
);


CREATE TABLE Inventory (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL
);




