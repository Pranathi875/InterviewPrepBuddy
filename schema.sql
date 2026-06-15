-- Interview Prep Buddy - Database Schema
-- Run this against your MySQL instance to set up the database.

CREATE DATABASE IF NOT EXISTS interview_prep_buddy;
USE interview_prep_buddy;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(500),
    topic VARCHAR(100),
    difficulty VARCHAR(10) NOT NULL,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    result VARCHAR(30) NOT NULL,
    attempted_at DATE NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);

CREATE TABLE IF NOT EXISTS review_schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL UNIQUE,
    next_review_date DATE NOT NULL,
    interval_days INT NOT NULL DEFAULT 1,
    FOREIGN KEY (question_id) REFERENCES questions(id)
);
