-- V2__sample_data.sql
-- Sample data for the Gradle80 application database
-- This script inserts sample data into users, products, orders, order items, and notifications tables

-- ==================== --
-- SAMPLE USERS DATA    --
-- ==================== --

-- Insert sample user data with bcrypt hashed passwords (all passwords are 'password123' for sample data)
INSERT INTO users (id, username, email, first_name, last_name, password_hash, active, created_at, updated_at) VALUES 
(nextval('hibernate_sequence'), 'admin', 'admin@gradle80.com', 'Admin', 'User', '$2a$10$qPEZvs/lAQXFvO7bNQK6QuBR/YLRR9XZk54ztXniRWTvhjyhV0yNG', true, now(), now()),
(nextval('hibernate_sequence'), 'johndoe', 'john.doe@example.com', 'John', 'Doe', '$2a$10$qPEZvs/lAQXFvO7bNQK6QuBR/YLRR9XZk54ztXniRWTvhjyhV0yNG', true, now(), now()),
(nextval('hibernate_sequence'), 'janedoe', 'jane.doe@example.com', 'Jane', 'Doe', '$2a$10$qPEZvs/lAQXFvO7bNQK6QuBR/YLRR9XZk54ztXniRWTvhjyhV0yNG', true, now(), now()),
(nextval('hibernate_sequence'), 'bobsmith', 'bob.smith@example.com', 'Bob', 'Smith', '$2a$10$qPEZvs/lAQXFvO7bNQK6QuBR/YLRR9XZk54ztXniRWTvhjyhV0yNG', true, now(), now()),
(nextval('hibernate_sequence'), 'alicejones', 'alice.jones@example.com', 'Alice', 'Jones', '$2a$10$qPEZvs/lAQXFvO7bNQK6QuBR/YLRR9XZk54ztXniRWTvhjyhV0yNG', true, now(), now()),
(nextval('hibernate_sequence'), 'inactive', 'inactive@example.com', 'Inactive', 'User', '$2a$10$qPEZvs/lAQXFvO7bNQK6QuBR/YLRR9XZk54ztXniRWTvhjyhV0yNG', false, now(), now());

-- ==================== --
-- SAMPLE PRODUCTS DATA --
-- ==================== --

-- Insert sample product data across different categories
INSERT INTO products (id, name, description, price, category, available, created_at, updated_at) VALUES 
-- Electronics category
(nextval('hibernate_sequence'), 'Laptop Pro X', 'High performance laptop with 16GB RAM and 512GB SSD', 1299.99, 'Electronics', true, now(), now()),
(nextval('hibernate_sequence'), 'Smartphone Ultra', 'Latest smartphone with dual camera and 128GB storage', 899.99, 'Electronics', true, now(), now()),
(nextval('hibernate_sequence'), 'Wireless Headphones', 'Noise cancelling wireless headphones with 20hr battery life', 249.99, 'Electronics', true, now(), now()),
(nextval('hibernate_sequence'), 'Smart Watch', 'Fitness and health tracking smartwatch', 199.99, 'Electronics', true, now(), now()),
(nextval('hibernate_sequence'), 'Tablet Mini', 'Compact tablet with 10-inch display', 399.99, 'Electronics', false, now(), now()),

-- Home & Kitchen category
(nextval('hibernate_sequence'), 'Coffee Maker', 'Programmable coffee maker with thermal carafe', 89.99, 'Home & Kitchen', true, now(), now()),
(nextval('hibernate_sequence'), 'Blender Pro', 'High-speed blender for smoothies and food processing', 79.99, 'Home & Kitchen', true, now(), now()),
(nextval('hibernate_sequence'), 'Air Fryer', '6-quart digital air fryer', 129.99, 'Home & Kitchen', true, now(), now()),
(nextval('hibernate_sequence'), 'Robot Vacuum', 'Smart robotic vacuum with mapping technology', 349.99, 'Home & Kitchen', true, now(), now()),
(nextval('hibernate_sequence'), 'Toaster Oven', '6-slice convection toaster oven', 119.99, 'Home & Kitchen', false, now(), now()),

-- Books category
(nextval('hibernate_sequence'), 'The Great Novel', 'Award-winning fiction bestseller', 24.99, 'Books', true, now(), now()),
(nextval('hibernate_sequence'), 'Coding for Beginners', 'Learn programming from scratch', 39.99, 'Books', true, now(), now()),
(nextval('hibernate_sequence'), 'Healthy Cooking', 'Cookbook with nutritious recipes', 32.99, 'Books', true, now(), now()),
(nextval('hibernate_sequence'), 'Business Strategy', 'Guide to modern business practices', 49.99, 'Books', false, now(), now()),
(nextval('hibernate_sequence'), 'History of Art', 'Comprehensive art history reference', 59.99, 'Books', true, now(), now()),

-- Clothing category
(nextval('hibernate_sequence'), 'Winter Jacket', 'Waterproof winter jacket with thermal lining', 149.99, 'Clothing', true, now(), now()),
(nextval('hibernate_sequence'), 'Running Shoes', 'Lightweight running shoes for professional athletes', 129.99, 'Clothing', true, now(), now()),
(nextval('hibernate_sequence'), 'Cotton T-Shirt', 'Pack of 3 premium cotton t-shirts', 34.99, 'Clothing', true, now(), now()),
(nextval('hibernate_sequence'), 'Jeans', 'Classic fit denim jeans', 59.99, 'Clothing', true, now(), now()),
(nextval('hibernate_sequence'), 'Wool Sweater', 'Soft wool sweater for winter', 79.99, 'Clothing', true, now(), now());

-- ==================== --
-- SAMPLE ORDERS DATA   --
-- ==================== --

-- Insert sample orders for different users
INSERT INTO orders (id, user_id, total_amount, status, shipping_address, created_at, updated_at) VALUES 
-- John Doe's orders
(nextval('hibernate_sequence'), 2, 1549.98, 'DELIVERED', '123 Main St, Anytown, CA 12345', (now() - interval '30 days'), (now() - interval '25 days')),
(nextval('hibernate_sequence'), 2, 279.98, 'SHIPPED', '123 Main St, Anytown, CA 12345', (now() - interval '10 days'), (now() - interval '9 days')),

-- Jane Doe's orders
(nextval('hibernate_sequence'), 3, 1149.98, 'DELIVERED', '456 Oak Ave, Somecity, NY 67890', (now() - interval '45 days'), (now() - interval '40 days')),
(nextval('hibernate_sequence'), 3, 169.98, 'PROCESSING', '456 Oak Ave, Somecity, NY 67890', now(), now()),

-- Bob Smith's orders
(nextval('hibernate_sequence'), 4, 399.98, 'DELIVERED', '789 Pine Rd, Otherville, TX 54321', (now() - interval '60 days'), (now() - interval '55 days')),
(nextval('hibernate_sequence'), 4, 99.98, 'CANCELLED', '789 Pine Rd, Otherville, TX 54321', (now() - interval '15 days'), (now() - interval '14 days')),

-- Alice Jones's orders
(nextval('hibernate_sequence'), 5, 599.96, 'PENDING', '321 Elm Blvd, Lastcity, FL 13579', now(), now());

-- ==================== --
-- SAMPLE ORDER ITEMS   --
-- ==================== --

-- Insert order items for each order
INSERT INTO order_items (id, order_id, product_id, quantity, price_at_order) VALUES 
-- John Doe's first order items
(nextval('hibernate_sequence'), 21, 7, 1, 1299.99),
(nextval('hibernate_sequence'), 21, 8, 1, 249.99),
-- John Doe's second order items
(nextval('hibernate_sequence'), 22, 11, 2, 89.99),
(nextval('hibernate_sequence'), 22, 13, 1, 99.99),

-- Jane Doe's first order items
(nextval('hibernate_sequence'), 23, 8, 1, 899.99),
(nextval('hibernate_sequence'), 23, 10, 1, 249.99),
-- Jane Doe's second order items
(nextval('hibernate_sequence'), 24, 12, 1, 79.99),
(nextval('hibernate_sequence'), 24, 16, 3, 29.99),

-- Bob Smith's first order items
(nextval('hibernate_sequence'), 25, 9, 2, 199.99),
-- Bob Smith's second order items
(nextval('hibernate_sequence'), 26, 16, 2, 49.99),

-- Alice Jones's order items
(nextval('hibernate_sequence'), 27, 20, 4, 149.99);

-- ==================== --
-- SAMPLE NOTIFICATIONS --
-- ==================== --

-- Insert sample notifications for users
INSERT INTO notifications (id, user_id, type, message, read, created_at) VALUES 
-- John Doe's notifications
(nextval('hibernate_sequence'), 2, 'EMAIL', 'Your order #21 has been delivered', true, (now() - interval '25 days')),
(nextval('hibernate_sequence'), 2, 'EMAIL', 'Your order #22 has been shipped', false, (now() - interval '9 days')),
(nextval('hibernate_sequence'), 2, 'SYSTEM', 'Welcome to our platform! Complete your profile to get personalized recommendations.', false, (now() - interval '60 days')),

-- Jane Doe's notifications
(nextval('hibernate_sequence'), 3, 'EMAIL', 'Your order #23 has been delivered', true, (now() - interval '40 days')),
(nextval('hibernate_sequence'), 3, 'PUSH', 'Flash sale: 20% off on Electronics today only!', false, (now() - interval '5 days')),
(nextval('hibernate_sequence'), 3, 'EMAIL', 'Your order #24 is being processed', false, now()),

-- Bob Smith's notifications
(nextval('hibernate_sequence'), 4, 'EMAIL', 'Your order #25 has been delivered', true, (now() - interval '55 days')),
(nextval('hibernate_sequence'), 4, 'EMAIL', 'Your order #26 has been cancelled', true, (now() - interval '14 days')),
(nextval('hibernate_sequence'), 4, 'SYSTEM', 'Your account has been inactive for 30 days', false, (now() - interval '10 days')),

-- Alice Jones's notifications
(nextval('hibernate_sequence'), 5, 'EMAIL', 'Your order #27 has been received', false, now()),
(nextval('hibernate_sequence'), 5, 'PUSH', 'New arrivals in your favorite category', false, (now() - interval '3 days')),
(nextval('hibernate_sequence'), 5, 'SYSTEM', 'Complete a survey to get 10% off your next purchase', false, (now() - interval '7 days'));

-- ==================== --
-- DATA VALIDATION      --
-- ==================== --

-- FIXME: Ensure price consistency between products and order_items for production data
-- TODO: Add more diverse product categories in future data migrations
-- TODO: Add user roles and permissions when role tables are created
-- NOTE: All passwords are hashed with bcrypt and set to 'password123' for demonstration purposes