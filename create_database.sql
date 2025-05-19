-- ================================================================
-- IoTBay Schema and Data (with DROP IF EXISTS, 3 Users, Payment)
-- ================================================================

DROP DATABASE IF EXISTS iotbaydb;
CREATE DATABASE iotbaydb;
USE iotbaydb;

DROP TABLE IF EXISTS PaymentItem;
DROP TABLE IF EXISTS Payment;
DROP TABLE IF EXISTS OrderItem;
DROP TABLE IF EXISTS `Order`;
DROP TABLE IF EXISTS Device;
DROP TABLE IF EXISTS `User`;

-- 1. Users
CREATE TABLE `User` (
    user_id      INT AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(100)    NOT NULL,
    email        VARCHAR(255)    NOT NULL UNIQUE,
    password     VARCHAR(255)    NOT NULL,
    phone        VARCHAR(20),
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Devices (IoT products)
CREATE TABLE Device (
    device_id    INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(150)    NOT NULL,
    type         VARCHAR(50)     NOT NULL,
    unit_price   DECIMAL(10,2)   NOT NULL CHECK (unit_price >= 0),
    stock        INT             NOT NULL CHECK (stock >= 0),
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Orders (formerly Carts)
CREATE TABLE `Order` (
    order_id     INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT,
    status       ENUM('draft','submitted','cancelled') NOT NULL DEFAULT 'draft',
    created_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES `User`(user_id) ON DELETE SET NULL
);

-- 4. OrderItems (formerly CartItems)
CREATE TABLE OrderItem (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id      INT             NOT NULL,
    device_id     INT             NOT NULL,
    quantity      INT             NOT NULL CHECK (quantity > 0),
    unit_price    DECIMAL(10,2)   NOT NULL CHECK (unit_price >= 0),
    added_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id)   REFERENCES `Order`(order_id)   ON DELETE CASCADE,
    FOREIGN KEY (device_id)  REFERENCES Device(device_id)   ON DELETE RESTRICT
);

-- 5. Payment Management Tables

-- 5.1 Payments
CREATE TABLE Payment (
    payment_id    INT AUTO_INCREMENT PRIMARY KEY,
    order_id      INT             NOT NULL,
    method        VARCHAR(50)     NOT NULL,           -- e.g. 'Credit Card'
    card_number   VARCHAR(20)     NOT NULL,           -- tokenized or masked
    amount        DECIMAL(10,2)   NOT NULL CHECK(amount >= 0),
    paid_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status        ENUM('completed','cancelled') NOT NULL DEFAULT 'completed',
    FOREIGN KEY (order_id) REFERENCES `Order`(order_id) ON DELETE CASCADE
);

-- 5.2 (Optional) Payment Items if multiple line items per payment
CREATE TABLE PaymentItem (
    payment_item_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_id      INT             NOT NULL,
    order_item_id   INT             NOT NULL,
    amount          DECIMAL(10,2)   NOT NULL CHECK(amount >= 0),
    FOREIGN KEY (payment_id)    REFERENCES Payment(payment_id)    ON DELETE CASCADE,
    FOREIGN KEY (order_item_id) REFERENCES OrderItem(order_item_id) ON DELETE CASCADE
);

-- ================================================================
-- Seed Data
-- ================================================================

-- Users (only 3 for demo)
INSERT INTO `User` (full_name, email, password, phone, created_at) VALUES
  ('Alice Zhang',  'alice.zhang@example.com',  'hashed_pw_1', '0400-111-222', '2025-04-01 09:12:00'),
  ('Bob Smith',    'bob.smith@example.com',    'hashed_pw_2', '0400-333-444', '2025-03-28 14:32:00'),
  ('Carol Nguyen', 'carol.nguyen@example.com', 'hashed_pw_3', '0400-555-666', '2025-04-15 11:05:00');

-- Devices
INSERT INTO Device (name, type, unit_price, stock, created_at) VALUES
  ('TempSense Pro',      'Sensor',     49.95, 100, '2025-03-01 10:00:00'),
  ('LightControl X',     'Actuator',   79.99,  50, '2025-02-15 12:30:00'),
  ('SmartCam HD',        'Camera',    129.50,  25, '2025-01-20 09:15:00'),
  ('AirQuality Monitor', 'Sensor',     59.00,  75, '2025-04-05 14:50:00'),
  ('DoorLock Secure',    'Actuator',   99.99,  40, '2025-03-18 11:25:00'),
  ('PowerMeter Lite',    'Meter',      39.95, 120, '2025-04-10 16:10:00');

-- Orders (mostly for user_id=1)
INSERT INTO `Order` (user_id, status, created_at, updated_at) VALUES
  (1, 'draft',     '2025-04-20 09:00:00', '2025-04-20 09:00:00'),
  (1, 'draft',     '2025-04-21 10:30:00', '2025-04-21 10:30:00'),
  (1, 'submitted', '2025-04-22 14:00:00', '2025-04-22 14:05:00'),
  (2, 'draft',     '2025-04-18 08:15:00', '2025-04-18 08:20:00'),
  (3, 'cancelled', '2025-04-23 11:11:00', '2025-04-23 11:11:00');

-- OrderItems
INSERT INTO OrderItem (order_id, device_id, quantity, unit_price, added_at) VALUES
  (1,  1,  2,  49.95, '2025-04-20 09:01:00'),
  (1,  4,  1,  59.00, '2025-04-20 09:02:00'),
  (2,  2,  3,  79.99, '2025-04-21 10:32:00'),
  (3,  3,  1, 129.50, '2025-04-22 14:01:00'),
  (4,  5,  2,  99.99, '2025-04-18 08:16:00');

-- Payments (linked to orders)
INSERT INTO Payment (order_id, method, card_number, amount, paid_at, status) VALUES
  (3, 'Credit Card', '****-****-****-1234', 129.50, '2025-04-22 14:06:00', 'completed'),
  (1, 'Credit Card', '****-****-****-5678', 159.90, '2025-04-20 09:05:00', 'completed'),
  (4, 'Credit Card', '****-****-****-9012', 199.98, '2025-04-18 08:25:00', 'cancelled');

-- (Optional) PaymentItems for line-level allocations
INSERT INTO PaymentItem (payment_id, order_item_id, amount) VALUES
  (1, 4, 129.50),
  (2, 1,  99.90),  -- e.g. sum of line items
  (3, 5, 199.98);

ALTER TABLE User ADD address VARCHAR(255);

ALTER TABLE User ADD user_type VARCHAR(255);

INSERT INTO `User` (full_name, email, password, phone, created_at, address, user_type) VALUES
  ('Admin', 'admin@iotbay.com', 'admin123', '0411111111', '2025-04-15 11:05:00', '123, Example St., Sydney', 'admin');

