-- Create the database
CREATE DATABASE CinemaCenter
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
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

SET SQL_SAFE_UPDATES = 0;
SET SQL_SAFE_UPDATES = 1;


-- Tickets table
CREATE TABLE Tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    seat_numbers TEXT NOT NULL,            -- Store seat numbers as comma-separated values
    discounted_seat_numbers TEXT,          -- Store discounted seat numbers as comma-separated values
    customer_name VARCHAR(100),
    total_seat_cost DECIMAL(10, 2),
    total_product_cost DECIMAL(10, 2),
    total_tax DECIMAL(10, 2),              -- Add total_tax column
    total_cost DECIMAL(10, 2),             -- Add total_cost column
    FOREIGN KEY (session_id) REFERENCES Sessions(session_id)
);

CREATE TABLE Invoices (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,        -- Unique identifier for the invoice
    ticket_id INT NOT NULL,                            -- Foreign key linking to the Tickets table
    invoice_format ENUM('PDF', 'HTML', 'TXT') NOT NULL, -- The format of the invoice (PDF, HTML, etc.)
    invoice_file BLOB NOT NULL,                        -- Binary data of the invoice file (PDF/HTML/TXT, etc.)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    -- Timestamp when the invoice was created
    FOREIGN KEY (ticket_id) REFERENCES Tickets(ticket_id) ON DELETE CASCADE -- Linking to Tickets table
);







-- Products table
CREATE TABLE Products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL,
    tax_rate DECIMAL(5, 2) NOT NULL,
    image BLOB
    
);




CREATE TABLE TicketPricing (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hall_name ENUM('Hall_A', 'Hall_B') NOT NULL,     -- Hall type (Hall_A or Hall_B)
    base_price DECIMAL(10, 2) NOT NULL,               -- Base price before discount
    discount_rate DECIMAL(5, 2) DEFAULT 50.00        -- Discount rate (50% for under 18 or over 60)
);

INSERT INTO TicketPricing (hall_name, base_price, discount_rate)
VALUES 
    ('Hall_A', 100.00, 50.00), 
    ('Hall_B', 120.00, 40.00);



-- Sample data
-- Users
INSERT INTO Users (first_name, last_name, username, password, role)
VALUES
('Emir', 'Özen', 'admin1', 'admin1', 'admin'),
('Teca', 'Kanadji', 'manager1', 'manager1', 'manager'),
('Taha', 'Özkan', 'cashier1', 'cashier1', 'cashier');
SELECT * FROM Users;
-- Movies
-- allows duplicates but it shouldnt i think
INSERT INTO Movies (title, poster_image, genre, summary, duration)
VALUES
('Inception', 'C:\\Users\\emiro\\Desktop\\me\\images\\inception.jpg', 'Sci-Fi', 'A mind-bending thriller about dreams within dreams.', 148),
('Titanic','C:\\Users\\emiro\\Desktop\\me\\images\\titanic.jpg' , 'Romance', 'A tragic love story aboard the ill-fated Titanic.', 195);

#TO MAKE IT BLOB
#LOAD_FILE('C:\\Users\\emiro\\Desktop\\me\\images\\inception.jpg')

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

DROP TABLE Seats;
-- Adding seats for Titanic session at 15:00 in Hall_B (session_id = 2)
INSERT INTO Seats (hall_name, seat_label, session_id, is_occupied)
VALUES
('Hall_B', 'A1', 4, FALSE),
('Hall_B', 'A2', 4, FALSE),
('Hall_B', 'A3', 4, FALSE),
('Hall_B', 'A4', 4, FALSE),
('Hall_B', 'A5', 4, FALSE),
('Hall_B', 'A6', 4, FALSE),
('Hall_B', 'B1', 4, FALSE),
('Hall_B', 'B2', 4, FALSE),
('Hall_B', 'B3', 4, FALSE),
('Hall_B', 'B4', 4, FALSE),
('Hall_B', 'B5', 4, FALSE),
('Hall_B', 'B6', 4, FALSE),
('Hall_B', 'C1', 4, FALSE),
('Hall_B', 'C2', 4, FALSE),
('Hall_B', 'C3', 4, FALSE),
('Hall_B', 'C4', 4, FALSE),
('Hall_B', 'C5', 4, FALSE),
('Hall_B', 'C6', 4, FALSE),
('Hall_B', 'D1', 4, FALSE),
('Hall_B', 'D2', 4, FALSE),
('Hall_B', 'D3', 4, FALSE),
('Hall_B', 'D4', 4, FALSE),
('Hall_B', 'D5', 4, FALSE),
('Hall_B', 'D6', 4, FALSE),
('Hall_B', 'E1', 4, FALSE),
('Hall_B', 'E2', 4, FALSE),
('Hall_B', 'E3', 4, FALSE),
('Hall_B', 'E4', 4, FALSE),
('Hall_B', 'E5', 4, FALSE),
('Hall_B', 'E6', 4, FALSE),
('Hall_B', 'F1', 4, FALSE),
('Hall_B', 'F2', 4, FALSE),
('Hall_B', 'F3', 4, FALSE),
('Hall_B', 'F4', 4, FALSE),
('Hall_B', 'F5', 4, FALSE),
('Hall_B', 'F6', 4, FALSE),
('Hall_B', 'G1', 4, FALSE),
('Hall_B', 'G2', 4, FALSE),
('Hall_B', 'G3', 4, FALSE),
('Hall_B', 'G4', 4, FALSE),
('Hall_B', 'G5', 4, FALSE),
('Hall_B', 'G6', 4, FALSE),
('Hall_B', 'H1', 4, FALSE),
('Hall_B', 'H2', 4, FALSE),
('Hall_B', 'H3', 4, FALSE),
('Hall_B', 'H4', 4, FALSE),
('Hall_B', 'H5', 4, FALSE),
('Hall_B', 'H6', 4, FALSE);





-- Products
INSERT INTO Products (name, price, stock_quantity, tax_rate, image)
VALUES
('Beverage', 5.00, 100, 0.10, 'C:/Users/emiro/Desktop/me/images/beverage.jpg'),
('Biscuit', 3.00, 200, 0.10, 'C:/Users/emiro/Desktop/me/images/biscuit.jpg'),
('Toy', 10.00, 50, 0.10, 'C:/Users/emiro/Desktop/me/images/toy.jpg');





