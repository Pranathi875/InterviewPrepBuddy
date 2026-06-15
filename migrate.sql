-- Run this ONLY if you already have the old database without users table.
-- This migrates your existing data to the new multi-user schema.

USE interview_prep_buddy;

-- 1. Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- 2. Add user_id column to questions (nullable first so existing rows don't break)
ALTER TABLE questions ADD COLUMN user_id INT AFTER id;

-- 3. Create a default user for your existing data
INSERT INTO users (username, password) VALUES ('admin', 'admin');

-- 4. Assign all existing questions to that default user
UPDATE questions SET user_id = 1;

-- 5. Now make user_id NOT NULL and add foreign key
ALTER TABLE questions MODIFY COLUMN user_id INT NOT NULL;
ALTER TABLE questions ADD FOREIGN KEY (user_id) REFERENCES users(id);

-- Done! Login with username: admin, password: admin to see your old data.
-- Then register a new account with your own username.
