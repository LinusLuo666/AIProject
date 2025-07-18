-- Database: auth_system
-- Create database if not exists
CREATE DATABASE IF NOT EXISTS auth_system;
USE auth_system;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    status TINYINT DEFAULT 1 COMMENT '1: active, 0: inactive',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    status TINYINT DEFAULT 1 COMMENT '1: active, 0: inactive',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Menus table (permissions)
CREATE TABLE IF NOT EXISTS menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    path VARCHAR(100),
    component VARCHAR(100),
    icon VARCHAR(50),
    parent_id BIGINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    permission VARCHAR(100) COMMENT 'permission identifier',
    menu_type TINYINT DEFAULT 1 COMMENT '1: menu, 2: button',
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- User-Role relationship table
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Role-Menu relationship table
CREATE TABLE IF NOT EXISTS role_menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_role_menu (role_id, menu_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, email, status) VALUES 
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'admin@example.com', 1),
('user1', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'user1@example.com', 1);

-- Insert default roles
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'System Administrator'),
('USER', 'Regular User'),
('MANAGER', 'Department Manager');

-- Insert default menus/permissions
INSERT INTO menus (name, path, component, icon, parent_id, sort_order, permission, menu_type) VALUES
('System Management', '/system', 'Layout', 'system', 0, 1, 'system:manage', 1),
('User Management', '/system/users', 'system/user/index', 'user', 1, 1, 'system:user', 1),
('Role Management', '/system/roles', 'system/role/index', 'role', 1, 2, 'system:role', 1),
('Menu Management', '/system/menus', 'system/menu/index', 'menu', 1, 3, 'system:menu', 1);

-- Insert role-menu relationships (ADMIN has all permissions)
INSERT INTO role_menus (role_id, menu_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 1), (2, 2),
(3, 1), (3, 2), (3, 3);

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),  -- admin has ADMIN role
(2, 2);  -- user1 has USER role