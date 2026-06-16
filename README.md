# Interview Prep Buddy — Phase 1 (Console App)

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-database-4479A1?logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-connector-blue)
![Platform](https://img.shields.io/badge/platform-macOS%20%7C%20Windows%20%7C%20Linux-lightgrey)

A Java console app that helps you practice DSA (Data Structures & Algorithms) using **spaced repetition**. You add problems you've attempted, rate how well you did, and the app tells you which problems to revisit today based on your performance.

---



## How It Works

1. You add LeetCode/DSA problems you've practiced
2. After each attempt, you log how it went (solved easily / with help / couldn't solve)
3. The app uses spaced repetition to schedule when you should revisit each problem
4. Each day, check "Today's Reviews" to see what to practice

---

## Tech Stack

- **Java 17** (pure Java, no frameworks)
- **MySQL** (stores all your data)
- **JDBC** (Java connects to MySQL via MySQL Connector/J)
- **Terminal/Console** (run from command line)

---

## Project Structure

```
InterviewPrepBuddy/
├── src/
│   ├── Main.java              ← Menu loop + login/register (Scanner + switch)
│   ├── User.java              ← User class (id, username, password hash)
│   ├── PasswordUtil.java      ← SHA-256 + salt password hashing
│   ├── Question.java          ← Question class (fields + getters/setters)
│   ├── Attempt.java           ← Attempt class
│   ├── ReviewSchedule.java    ← Tracks next review date + interval
│   ├── DatabaseHelper.java    ← JDBC connection + all SQL queries
│   └── SpacedRepetition.java  ← The algorithm (switch + date math)
├── lib/
│   └── mysql-connector-j.jar  ← MySQL JDBC driver (you download this)
├── schema.sql                 ← Database setup script (fresh install)
├── migrate.sql                ← Migration script (upgrade old single-user DB)
├── .env.example               ← Template for DB credentials
├── run.sh                     ← macOS/Linux build + run script
├── run.bat                    ← Windows build + run script
└── README.md                  ← This file
```

---


### Prerequisites
- Java 17+ — Download from https://adoptium.net/ (pick Windows x64 .msi)
- MySQL — Download from https://dev.mysql.com/downloads/installer/




## Database Schema

The app uses 4 tables in a MySQL database called `interview_prep_buddy`:

### `users` — Accounts
| Column   | Type         | Purpose                          |
|----------|--------------|----------------------------------|
| id       | INT (auto)   | Primary key                      |
| username | VARCHAR(50)  | Unique login name                |
| password | VARCHAR(255) | Hashed password (salt:hash)      |

### `questions` — Your problem list
| Column     | Type         | Purpose                              |
|-----------|--------------|--------------------------------------|
| id        | INT (auto)   | Primary key                          |
| user_id   | INT (FK)     | Which user owns this question        |
| title     | VARCHAR(255) | Problem name (e.g., "Two Sum")       |
| url       | VARCHAR(500) | LeetCode link                        |
| topic     | VARCHAR(100) | Array, Stack, Trees, etc.            |
| difficulty| VARCHAR(10)  | EASY, MEDIUM, HARD                   |
| notes     | TEXT         | Your approach / notes (optional)     |

### `attempts` — Every time you practice a problem
| Column       | Type        | Purpose                                    |
|-------------|-------------|--------------------------------------------|
| id          | INT (auto)  | Primary key                                |
| question_id | INT (FK)    | Which question                             |
| result      | VARCHAR(30) | SOLVED_EASILY, SOLVED_WITH_HELP, COULD_NOT_SOLVE |
| attempted_at| DATE        | When you tried it                          |

### `review_schedule` — When to revisit each problem
| Column          | Type      | Purpose                         |
|----------------|-----------|----------------------------------|
| id             | INT (auto)| Primary key                      |
| question_id    | INT (FK)  | Which question (unique)          |
| next_review_date| DATE     | When to revisit                  |
| interval_days  | INT       | Current gap between reviews      |

---

## Spaced Repetition Algorithm

Located in `src/SpacedRepetition.java`. Simple logic:


When you log an attempt:
  SOLVED_EASILY    → interval = interval × 2  (e.g., 1 → 2 → 4 → 8 → 16 days)
  SOLVED_WITH_HELP → interval stays the same
  COULD_NOT_SOLVE  → interval resets to 1 day

next_review_date = today + new interval



## Menu Options Explained

=== Menu ===
1. Add a question         → Enter title, URL, topic, difficulty, notes
2. List all questions     → Shows everything you've added
3. Log an attempt         → Pick a question, rate how it went → updates schedule
4. View today's reviews   → Shows problems due for review today (or overdue)
5. View stats by topic    → Table showing attempts and results per topic
6. Open question in browser → Opens the LeetCode link in your default browser
7. Exit


---


