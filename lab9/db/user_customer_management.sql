USE customer_management;
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
INSERT INTO users (username, email, password, full_name, role, is_active) VALUES
('admin', 'admin@example.com', '$2a$10$I8Yj.liUK.Rip1iYkEosQuLQy3qvdY8xf2sLjqZw4B6.GpyqnIJEK', 'Admin User', 'ADMIN', true),
('john', 'john@example.com', '$2a$10$I8Yj.liUK.Rip1iYkEosQuLQy3qvdY8xf2sLjqZw4B6.GpyqnIJEK', 'John Doe', 'USER', true),
('jane', 'jane@example.com', '$2a$10$I8Yj.liUK.Rip1iYkEosQuLQy3qvdY8xf2sLjqZw4B6.GpyqnIJEK', 'Jane Smith', 'USER', true);