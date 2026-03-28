-- Drop old tables if they already exist
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create users table (matches UserDAO: username + password)
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create messages table (matches MessageDAO queries)
CREATE TABLE messages (
    message_id SERIAL PRIMARY KEY,
    sender_username VARCHAR(50) NOT NULL,
    message_content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_username) REFERENCES users(username) ON DELETE CASCADE
);

-- Helpful indexes for history and user filtering
CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender_username);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp);