-- Create messages table for chat history
CREATE TABLE IF NOT EXISTS messages (
    message_id SERIAL PRIMARY KEY,
    sender_username VARCHAR(100) NOT NULL,
    message_content TEXT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_username) REFERENCES users(username) ON DELETE CASCADE
);

-- Create index on sender_username for faster queries
CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender_username);
CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp);
