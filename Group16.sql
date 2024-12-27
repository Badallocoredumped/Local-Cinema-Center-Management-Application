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
    surname VARCHAR(100) NOT NULL,
    phone_no VARCHAR(25),
    date_of_birth DATE,
    date_of_start DATE,
    email VARCHAR(100),
    DEFAULT_PASSWORD BOOLEAN DEFAULT TRUE
);

SELECT * FROM employees;
/*We will create the database manually*/
INSERT INTO employees (username, password, role, name, surname, phone_no, date_of_birth, date_of_start, email, DEFAULT_PASSWORD)
VALUES 
('emir5757', 'manager123', 'Manager', 'Emir', 'Özen', '+905551234567', '1980-01-01', '2010-05-15', 'emirozen57@hotmail.com', FALSE),
('Teca7', 'WeLoveTeca', 'Admin', 'Ahmed Marcolino Teca', 'Kanadji', '+905441234567', '1980-01-01', '2010-05-15', 'teca.kanadji@example.com', FALSE),
('manager1', 'manager1', 'Manager', 'Mana', 'Ger', '+905111111111', '1981-01-01', '2010-05-15', 'manager1@example.com', FALSE),
('admin1', 'admin1', 'Admin', 'Ad', 'Min', '+905222222222', '1982-01-01', '2010-05-15', 'admin1@example.com', FALSE),
('cashier1', 'cashier1', 'Cashier', 'Cash', 'Ier', '+905333333333', '1983-01-01', '2010-05-15', 'cashier1@example.com', FALSE);

CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    surname VARCHAR(50),
    age INT
);



/*Admin related tables*/
CREATE TABLE Movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    poster VARCHAR(255),
    genre VARCHAR(255), -- Comma-separated genres or normalize into a separate table
    summary TEXT
);

CREATE TABLE Halls (
    hall_id INT AUTO_INCREMENT PRIMARY KEY,
    hall_name VARCHAR(50) UNIQUE NOT NULL,
    capacity INT NOT NULL
);
SELECT * FROM Halls;
INSERT INTO Halls (hall_name, capacity)
VALUES
('Hall_A', '16'),
('Hall_B', '48');

CREATE TABLE Schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    hall_id INT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    sold_ticket BOOLEAN DEFAULT FALSE, /*Checks if tickets were sold for this session*/
    FOREIGN KEY (movie_id) REFERENCES Movies(movie_id) ON DELETE CASCADE,
    FOREIGN KEY (hall_id) REFERENCES Halls(hall_id) ON DELETE CASCADE
);

CREATE TABLE Tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    schedule_id INT NOT NULL,
    seat_id INT NOT NULL,
    customer_id INT,
    price DECIMAL(10, 2) NOT NULL,
    discount_applied BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (schedule_id) REFERENCES Schedules(schedule_id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES Seats(seat_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE SET NULL
);

CREATE TABLE Inventory (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL
);

CREATE TABLE Carts (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE CartItems (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES Carts(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES Inventory(product_id) ON DELETE CASCADE
);




