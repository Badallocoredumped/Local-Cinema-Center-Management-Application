-- Create the database
CREATE DATABASE CinemaCenter;
USE CinemaCenter;

-- Users table
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('cashier', 'admin', 'manager') NOT NULL
);

-- Movies table
CREATE TABLE Movies (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    poster_url VARCHAR(255),
    genre VARCHAR(100),
    summary TEXT
);

-- Halls table
CREATE TABLE Halls (
    hall_id INT AUTO_INCREMENT PRIMARY KEY,
    hall_name ENUM('Hall_A', 'Hall_B') UNIQUE NOT NULL,
    capacity INT NOT NULL
);

-- Sessions table
CREATE TABLE Sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    hall_name ENUM('Hall_A', 'Hall_B'),
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    vacant_seats INT NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES Movies(movie_id),
    FOREIGN KEY (hall_name) REFERENCES Halls(hall_name)
);

-- Tickets table
CREATE TABLE Tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    seat_number INT NOT NULL,
    customer_name VARCHAR(100),
    age INT,
    FOREIGN KEY (session_id) REFERENCES Sessions(session_id)
);

-- Products table
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL,
    tax_rate DECIMAL(5, 2) NOT NULL
);

-- Sample data
-- Users
INSERT INTO Users (first_name, last_name, username, password, role)
VALUES
('John', 'Doe', 'admin', 'password123', 'admin'),
('Jane', 'Smith', 'manager', 'password123', 'manager'),
('Emily', 'Brown', 'cashier', 'password123', 'cashier');

-- Movies
INSERT INTO Movies (title, poster_url, genre, summary)
VALUES
('Inception', '/path/to/inception.jpg', 'Sci-Fi', 'A mind-bending thriller about dreams within dreams.'),
('Titanic', '/path/to/titanic.jpg', 'Romance', 'A tragic love story aboard the ill-fated Titanic.');

-- Halls
INSERT INTO Halls (hall_name, capacity)
VALUES
('Hall_A', 16),
('Hall_B', 48);

-- Products
INSERT INTO Products (name, price, stock_quantity, tax_rate)
VALUES
('Beverage', 5.00, 100, 0.10),
('Biscuit', 3.00, 200, 0.10),
('Toy', 10.00, 50, 0.10);
