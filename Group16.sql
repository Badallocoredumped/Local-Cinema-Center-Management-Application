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
    status ENUM('ACTIVE', 'CANCELLED') DEFAULT 'ACTIVE', -- Status column for ticket
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
    image BLOB,
    category VARCHAR(50) NOT NULL  -- Added category column
);

CREATE TABLE TicketPricing (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hall_name ENUM('Hall_A', 'Hall_B') NOT NULL,     -- Hall type (Hall_A or Hall_B)
    base_price DECIMAL(10, 2) NOT NULL,               -- Base price before discount
    discount_rate DECIMAL(5, 2) DEFAULT 50.00        -- Discount rate (50% for under 18 or over 60)
);

INSERT INTO TicketPricing (hall_name, base_price, discount_rate)
VALUES 
    ('Hall_A', 10.00, 50.00), 
    ('Hall_B', 20.00, 50.00);


-- Users
INSERT INTO Users (first_name, last_name, username, password, role)
VALUES
('Emir', 'Özen', 'admin1', 'admin1', 'admin'),
('Teca', 'Kanadji', 'manager1', 'manager1', 'manager'),
('Taha', 'Özkan', 'cashier1', 'cashier1', 'cashier');

-- Movies
-- allows duplicates but it shouldnt i think
INSERT INTO Movies (title, poster_image, genre, summary, duration)
VALUES
('Inception', LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\inception.jpg'), 'Sci-Fi', 'A mind-bending thriller about dreams within dreams.', 120),
('Titanic', LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\titanic.jpg'), 'Romance', 'A tragic love story aboard the ill-fated Titanic.', 120),
('Blue Velvet', LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\bluevelvet.jpg'), 'Horror', 'Blue Velvet (1986) follows Jeffrey Beaumont, a young man who uncovers a dark, violent world beneath the surface of his small-town suburb after discovering a severed ear. As he investigates, he becomes entangled with a troubled woman and a sadistic criminal, revealing a sinister undercurrent in his seemingly idyllic town.', 120),
('A Clockwork Orange', LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\aclockworkorange.jpg'), 'Horror', 'A Clockwork Orange (1971), directed by Stanley Kubrick, is a dystopian crime film based on Anthony Burgess novel. It follows Alex DeLarge (Malcolm McDowell), a delinquent youth leading a gang that engages in violence and mayhem. After a robbery gone wrong, Alex is arrested and subjected to an experimental rehabilitation method that conditions him to associate violent impulses with intense physical discomfort. The film explores themes of free will, social control, and the nature of evil, set against a bleak, hyper-stylized future society.', 120),
('It Follows', LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\itfollows.jpg'), 'Adventure', 'It follows a young woman who is stalked by a supernatural force after a sexual encounter. The film explores themes of mortality, fear, and the consequences of actions.', 120);




-- Halls
INSERT INTO Halls (hall_name, seat_capacity)
VALUES
('Hall_A', 16),
('Hall_B', 48);

-- Sample Sessions data
-- Insert multiple sessions for Inception (movie_id = 1)




INSERT INTO Products (name, price, stock_quantity, tax_rate, image, category) 
VALUES
('Mineral Water', 5.00, 125, 0.10, LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\mineral-water.png'), 'Drinks'),
('Teddy Bear', 10.00, 45, 0.10, LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\teddy-bear.png'), 'Toys'),
('Cola', 3.75, 125, 0.10, LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\bottle.png'), 'Drinks'),
('Chips', 3.00, 125, 0.10, LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\snack.png'), 'Snacks'),
('Water', 1.25, 125, 0.10, LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\water.png'), 'Drinks'),
('Popcorn', 1.00, 125, 0.10, LOAD_FILE('C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Uploads\\popcorn.png'), 'Snacks');


#This might cause an issue will try tomorrow
-- Create Ticket_Products table
CREATE TABLE Ticket_Products (
    ticket_id INT NOT NULL,                -- Reference to the Tickets table
    product_name VARCHAR(255) NOT NULL,     -- Product name (references Products table)
    quantity INT NOT NULL,                 -- Quantity of the product purchased
    price DECIMAL(10, 2) NOT NULL,         -- Price of the product at the time of purchase
    PRIMARY KEY (ticket_id, product_name), -- Primary key on ticket_id and product_name
    FOREIGN KEY (ticket_id) REFERENCES Tickets(ticket_id) ON DELETE CASCADE,  -- Foreign key to Tickets table
    FOREIGN KEY (product_name) REFERENCES Products(name) ON DELETE CASCADE  -- Foreign key to Products table
);
CREATE TABLE bank (
    id INT AUTO_INCREMENT PRIMARY KEY,        -- Unique identifier for each record
    total_revenue DECIMAL(15, 2),             -- Column for total revenue
    tax_to_be_paid DECIMAL(15, 2)             -- Column for tax to be paid
);

INSERT INTO bank (total_revenue, tax_to_be_paid)
VALUES (0, 0);



