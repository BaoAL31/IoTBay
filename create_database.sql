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

INSERT INTO user (full_name, email, password, phone, created_at, address, user_type) VALUES
('Alice Bong', 'alice.zhang@example.com', 'alice123', '0400111222', '2025-04-01 09:12:00', '12, Station St.', 'user'),
('Bob Smith', 'bob.smith@example.com', 'BobS256', '0400333444', '2025-03-28 14:32:00', '98, McKeane St.', 'user'),
('John Doe', 'john.doe@gmail.com', 'John123', '0411221122', '2025-05-20 21:30:37', '2, Leppington St.', 'user'),
('Charlie Adams', 'charlie.adams@example.com', 'Charlie321', '0400111100', '2025-05-01 08:00:00', '456, Garden Ave', 'user'),
('Emma Watson', 'emma.watson@example.com', 'EmmaPass1', '0411333444', '2025-04-10 15:10:00', '789, Sunset Blvd', 'user'),
('Liam Wong', 'liam.wong@example.com', 'LiamWongPwd', '0422333444', '2025-03-20 10:25:00', '12, Harbour St.', 'user'),
('Noah Tan', 'noah.tan@example.com', 'N0ahPwd!', '0433221100', '2025-04-05 11:45:00', '55, Pitt St.', 'user'),
('Olivia Lim', 'olivia.lim@example.com', 'Olivia123', '0455667788', '2025-04-15 12:30:00', '88, Park Lane', 'user'),
('Sophia Kim', 'sophia.kim@example.com', 'S0phiaK', '0411444555', '2025-05-05 14:00:00', '22, Bond Street', 'user'),
('James Lee', 'james.lee@example.com', 'JamesLee1', '0400888777', '2025-03-29 09:15:00', '101, George St.', 'user'),
('Isabella Chen', 'isabella.chen@example.com', 'Isabella@456', '0422998877', '2025-03-25 17:00:00', '77, Regent St.', 'user'),
('Ethan Brown', 'ethan.brown@example.com', 'EthanB!', '0433996655', '2025-05-01 10:00:00', '64, Broadway', 'user'),
('Mia Taylor', 'mia.taylor@example.com', 'MiaPass', '0444112233', '2025-04-12 16:45:00', '19, College St.', 'user'),
('Lucas White', 'lucas.white@example.com', 'LucasW1', '0411888999', '2025-03-30 13:25:00', '33, Clarence St.', 'user'),
('Aria Singh', 'aria.singh@example.com', 'AriaSecure', '0422113344', '2025-05-06 09:00:00', '44, Liverpool St.', 'user'),
('Mason Patel', 'mason.patel@example.com', 'Mason123!', '0433445566', '2025-04-20 15:40:00', '88, Harris St.', 'user'),
('Ella Wong', 'ella.wong@example.com', 'EllaWongPwd', '0400777755', '2025-05-12 11:20:00', '14, Sussex St.', 'user'),
('Henry Zhao', 'henry.zhao@example.com', 'HenryZ88', '0411555777', '2025-05-14 10:10:00', '9, York St.', 'user'),
('Grace Tan', 'grace.tan@example.com', 'GracePwd1', '0422111199', '2025-04-08 12:00:00', '6, King St.', 'user'),
('Benjamin Tran', 'ben.tran@example.com', 'BenTran2025', '0455660001', '2025-03-27 18:30:00', '3, Elizabeth St.', 'user');

INSERT INTO user (full_name, email, password, phone, created_at, address, user_type) VALUES
('Mahidi Ilangarathna', 'mahdidi.i@iotbay.com', 'Mahidi@098', '0400101212', '2025-04-01 09:12:00', '12, King St.', 'staff'),
('Hoang Bao Nguyen', 'bao.n@iotbay.com', 'Bao=123', '0400333444', '2025-03-28 14:32:00', '98, College St.', 'staff'),
('Junayeed Halim', 'junayeed.h@iotbay.com', 'JHalim=345', '0431221022', '2025-05-20 21:30:37', '2, Bond Street', 'staff'),
('Sanija Abeywickrama', 'sanija.a@iotbay.com', 'Sanija456', '0400111100', '2025-05-01 08:00:00', '456, Harris St.', 'staff');

INSERT INTO device (name, type, unit_price, stock, created_at) VALUES
('SmartLight Mini', 'Sensor', 39.99, 100, '2025-01-12 09:00:00'),
('MotionSense Pro', 'Sensor', 69.50, 80, '2025-01-15 11:30:00'),
('DoorCam Elite', 'Camera', 149.99, 45, '2025-01-18 14:20:00'),
('TempMonitor X', 'Sensor', 44.90, 60, '2025-01-22 10:00:00'),
('EnviroCheck', 'Sensor', 89.00, 70, '2025-02-01 13:45:00'),
('LockGuard', 'Actuator', 105.75, 30, '2025-02-05 16:10:00'),
('Surveil 360', 'Camera', 199.00, 20, '2025-02-10 08:00:00'),
('AutoGate Control', 'Actuator', 125.99, 25, '2025-02-15 12:00:00'),
('ThermaCheck', 'Sensor', 59.99, 75, '2025-02-20 09:30:00'),
('AirSense Lite', 'Sensor', 49.95, 50, '2025-02-25 15:20:00'),
('StreamCam', 'Camera', 139.95, 35, '2025-03-01 10:00:00'),
('Humidity Pro', 'Sensor', 55.50, 90, '2025-03-05 11:15:00'),
('AccessBot', 'Actuator', 110.25, 40, '2025-03-10 13:00:00'),
('SmartValve', 'Actuator', 88.88, 70, '2025-03-15 14:40:00'),
('EcoView', 'Camera', 129.99, 25, '2025-03-20 10:45:00'),
('SafeLock X', 'Actuator', 95.45, 55, '2025-03-25 16:25:00'),
('InfraMonitor', 'Sensor', 60.60, 100, '2025-04-01 12:00:00'),
('AirGuard Max', 'Sensor', 72.30, 80, '2025-04-04 09:45:00'),
('CamWatch Ultra', 'Camera', 180.00, 15, '2025-04-08 08:30:00'),
('GateKeeper', 'Actuator', 120.00, 20, '2025-04-10 17:00:00');

ALTER TABLE user ADD COLUMN status VARCHAR(20) DEFAULT 'activated';

CREATE TABLE access_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    access_type VARCHAR(10), -- login or logout
    access_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

