-- Database Schema for Blockchain-Based Secure Cloud File Sharing System

-- USERS TABLE
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- FILES TABLE
CREATE TABLE files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    owner_id BIGINT NOT NULL,
    file_hash VARCHAR(64) NOT NULL, -- SHA-256 hash
    encryption_key VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (owner_id) REFERENCES users(id)
);

-- FILE ACCESS TABLE (for sharing and permissions)
CREATE TABLE file_access (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    permission_type ENUM('READ', 'WRITE', 'SHARE', 'DELETE') DEFAULT 'READ',
    granted_by BIGINT NOT NULL, -- User who granted the access
    granted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (granted_by) REFERENCES users(id),
    UNIQUE KEY unique_file_user (file_id, user_id)
);

-- BLOCKCHAIN BLOCKS TABLE (stores blockchain data)
CREATE TABLE blockchain_blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    block_index BIGINT NOT NULL,
    previous_hash VARCHAR(64) NOT NULL, -- Hash of previous block
    hash VARCHAR(64) NOT NULL, -- Current block hash
    data TEXT NOT NULL, -- JSON containing file operation details
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nonce INT NOT NULL DEFAULT 0,
    UNIQUE KEY unique_block_index (block_index)
);

-- ACTIVITY LOG TABLE
CREATE TABLE activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    file_id BIGINT,
    action VARCHAR(50) NOT NULL, -- UPLOAD, DOWNLOAD, DELETE, SHARE, VIEW
    action_details TEXT, -- Additional details about the action
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (file_id) REFERENCES files(id)
);

-- FEEDBACK TABLE
CREATE TABLE feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    rating TINYINT, -- 1-5 scale
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    is_resolved BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- TAMPERED FILES TABLE (for tracking detected tampered files)
CREATE TABLE tampered_files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_id BIGINT NOT NULL,
    detected_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    original_hash VARCHAR(64) NOT NULL,
    current_hash VARCHAR(64) NOT NULL,
    detected_by_user_id BIGINT,
    file_backup_path VARCHAR(500), -- Path to backup if available
    status ENUM('DETECTED', 'RECOVERED', 'IRRECOVERABLE') DEFAULT 'DETECTED',
    notes TEXT,
    FOREIGN KEY (file_id) REFERENCES files(id),
    FOREIGN KEY (detected_by_user_id) REFERENCES users(id)
);

-- CREATE INDEXES FOR BETTER PERFORMANCE
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_files_owner ON files(owner_id);
CREATE INDEX idx_files_hash ON files(file_hash);
CREATE INDEX idx_file_access_file ON file_access(file_id);
CREATE INDEX idx_file_access_user ON file_access(user_id);
CREATE INDEX idx_activity_log_user ON activity_log(user_id);
CREATE INDEX idx_activity_log_file ON activity_log(file_id);
CREATE INDEX idx_activity_log_action ON activity_log(action);
CREATE INDEX idx_activity_log_timestamp ON activity_log(created_at);
CREATE INDEX idx_feedback_user ON feedback(user_id);