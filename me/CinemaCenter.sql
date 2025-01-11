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
    poster_image LONGBLOB,
    genre VARCHAR(100),
    summary TEXT,
    duration INT NOT NULL
);
-- Halls table
CREATE TABLE Halls (
    hall_name ENUM('Hall_A', 'Hall_B') PRIMARY KEY,
    seat_capacity INT NOT NULL
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

-- Create the Seats table
CREATE TABLE Seats (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    hall_name ENUM('Hall_A', 'Hall_B'),
    seat_label VARCHAR(255) NOT NULL,
    session_id INT,  -- Optional: Can link to a specific session
    is_occupied BOOLEAN DEFAULT FALSE,  -- To track if the seat is taken
    FOREIGN KEY (hall_name) REFERENCES Halls(hall_name),
    FOREIGN KEY (session_id) REFERENCES Sessions(session_id)
);

-- A way to upload JPG files to the database
SET SQL_SAFE_UPDATES = 0;

UPDATE Movies
SET poster_url = 'C:\\Users\\emiro\\Desktop\\me\\images\\inception.jpg'
WHERE title = 'Inception';


SET SQL_SAFE_UPDATES = 1;

-

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
    tax_rate DECIMAL(5, 2) NOT NULL,
    image_path VARCHAR(255)
);



-- Sample data
-- Users
INSERT INTO Users (first_name, last_name, username, password, role)
VALUES
('John', 'Doe', 'admin', 'password123', 'admin'),
('Jane', 'Smith', 'manager', 'password123', 'manager'),
('Emily', 'Brown', 'cashier', 'password123', 'cashier');

-- Movies
-- allows duplicates but it shouldnt i think
INSERT INTO Movies (title, poster_url, genre, summary, duration)
VALUES
('Inception', 'C:\\Users\\emiro\\Desktop\\me\\images\\inception.jpg', 'Sci-Fi', 'A mind-bending thriller about dreams within dreams.', 148),
('Titanic', 'C:\\Users\\emiro\\Desktop\\me\\images\\inception.jpg', 'Romance', 'A tragic love story aboard the ill-fated Titanic.', 195);



-- Halls
INSERT INTO Halls (hall_name, seat_capacity)
VALUES
('Hall_A', 16),
('Hall_B', 48);

-- Sample Sessions data
-- Insert multiple sessions for Inception (movie_id = 1)
INSERT INTO Sessions (movie_id, hall_name, session_date, start_time, vacant_seats)
VALUES
(1, 'Hall_A', '2025-01-10', '14:00:00', 16),
(1, 'Hall_A', '2025-01-10', '17:00:00', 16),
(1, 'Hall_A', '2025-01-10', '20:00:00', 16);

-- Insert multiple sessions for Titanic (movie_id = 2)
INSERT INTO Sessions (movie_id, hall_name, session_date, start_time, vacant_seats)
VALUES
(2, 'Hall_B', '2025-01-10', '15:00:00', 48),
(2, 'Hall_B', '2025-01-10', '18:00:00', 48),
(2, 'Hall_B', '2025-01-10', '21:00:00', 48);



-- Inserting sample seats for Hall_A (session_id 1)
-- Adding seats for Inception session at 14:00 in Hall_A (session_id = 1)
-- Adding seats for Session 1 in Hall_A
INSERT INTO Seats (hall_name, seat_label, session_id, is_occupied)
VALUES
('Hall_A', 'A1', 1, FALSE),
('Hall_A', 'A2', 1, FALSE),
('Hall_A', 'A3', 1, FALSE),
('Hall_A', 'A4', 1, FALSE),
('Hall_A', 'B1', 1, FALSE),
('Hall_A', 'B2', 1, FALSE),
('Hall_A', 'B3', 1, FALSE),
('Hall_A', 'B4', 1, FALSE),
('Hall_A', 'C1', 1, FALSE),
('Hall_A', 'C2', 1, FALSE),
('Hall_A', 'C3', 1, FALSE),
('Hall_A', 'C4', 1, FALSE),
('Hall_A', 'D1', 1, FALSE),
('Hall_A', 'D2', 1, FALSE),
('Hall_A', 'D3', 1, FALSE),
('Hall_A', 'D4', 1, FALSE);


-- Adding seats for Titanic session at 15:00 in Hall_B (session_id = 2)
INSERT INTO Seats (hall_name, seat_label, session_id, is_occupied)
VALUES
('Hall_B', 'B1', 2, FALSE),
('Hall_B', 'B2', 2, TRUE),  -- Occupied seat
('Hall_B', 'B3', 2, FALSE),
('Hall_B', 'B4', 2, FALSE);




-- Products
INSERT INTO Products (name, price, stock_quantity, tax_rate, image_path)
VALUES
('Beverage', 5.00, 100, 0.10, 'C:/Users/emiro/Desktop/me/images/beverage.jpg'),
('Biscuit', 3.00, 200, 0.10, 'C:/Users/emiro/Desktop/me/images/biscuit.jpg'),
('Toy', 10.00, 50, 0.10, 'C:/Users/emiro/Desktop/me/images/toy.jpg');







-- Select movies
SELECT * FROM Movies;
SELECT session_date, start_time, hall_name, vacant_seats FROM Sessions WHERE movie_id = 2;
SELECT * FROM Sessions;
SELECT * FROM Seats;
SELECT * FROM Halls;
SELECT * FROM Products;
SELECT * FROM Sessions;
SELECT * FROM Sessions WHERE session_id = 13;


