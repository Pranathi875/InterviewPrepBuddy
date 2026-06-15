# Interview Prep Buddy — Phase 1 (Console App)

![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-database-4479A1?logo=mysql&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-connector-blue)
![Platform](https://img.shields.io/badge/platform-macOS%20%7C%20Windows%20%7C%20Linux-lightgrey)

A Java console app that helps you practice DSA (Data Structures & Algorithms) using **spaced repetition**. You add problems you've attempted, rate how well you did, and the app tells you which problems to revisit today based on your performance.

---

## Table of Contents

1. [How It Works](#how-it-works)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [Setup — macOS](#setup--macos)
5. [Setup — Windows (HP laptop, etc.)](#setup--windows)
6. [Setup — Linux](#setup--linux)
7. [Database Schema](#database-schema)
8. [Spaced Repetition Algorithm](#spaced-repetition-algorithm)
9. [Menu Options Explained](#menu-options-explained)
10. [Authentication / Multi-User](#authentication--multi-user)
11. [Common Issues](#common-issues)
12. [Making Changes (Without Kiro)](#making-changes-without-kiro)

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

## Setup — macOS

### Prerequisites
- Java 17+ (`java --version` to check)
- Homebrew (for installing MySQL)

### Steps

```bash
# 1. Navigate to project
cd ~/AndroidStudioProjects/InterviewPrepBuddy

# 2. Install and start MySQL
brew install mysql
brew services start mysql

# 3. Create the database and tables
mysql -u root < schema.sql

# 4. Download the JDBC driver
mkdir -p lib
curl -L -o lib/mysql-connector-j.jar "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar"

# 5. Set your MySQL credentials (if your MySQL has a password)
cp .env.example .env
# Then edit .env and set DB_PASSWORD=yourpassword

# 6. Build and run
chmod +x run.sh
./run.sh
```

### To run again later (after reboot)
```bash
brew services start mysql   # make sure MySQL is running
cd ~/AndroidStudioProjects/InterviewPrepBuddy
./run.sh
```

---

## Setup — Windows

### Prerequisites
- Java 17+ — Download from https://adoptium.net/ (pick Windows x64 .msi)
- MySQL — Download from https://dev.mysql.com/downloads/installer/

### Steps

```cmd
REM 1. Install Java 17 from Adoptium (add to PATH during install)

REM 2. Install MySQL using the MySQL Installer
REM    - Choose "Developer Default" or "Server Only"
REM    - Set a root password during setup (remember it!)
REM    - Make sure MySQL Server is running as a Windows Service

REM 3. Open Command Prompt, navigate to project folder
cd C:\path\to\InterviewPrepBuddy

REM 4. Run the schema (using MySQL command line client)
mysql -u root -p < schema.sql
REM    (enter your password when prompted)

REM 5. Download the JDBC driver
mkdir lib
curl -L -o lib\mysql-connector-j.jar "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar"
REM    OR just download that URL in your browser and save to lib\ folder

REM 6. Set your MySQL credentials
copy .env.example .env
REM    Then edit .env and set DB_PASSWORD=yourpassword

REM 7. Compile
mkdir out
javac -cp "lib\*" -d out src\*.java

REM 8. Run
java -cp "out;lib\*" Main
```

**IMPORTANT for Windows:** The classpath separator is `;` (semicolon), NOT `:` (colon).

### To run again later
```cmd
REM Just compile and run (MySQL runs as a service automatically)
cd C:\path\to\InterviewPrepBuddy
javac -cp "lib\*" -d out src\*.java
java -cp "out;lib\*" Main
```

### Optional: Create a run.bat file
Create a file called `run.bat` in the project root:
```bat
@echo off
echo Compiling...
mkdir out 2>nul
javac -cp "lib\*" -d out src\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Starting Interview Prep Buddy...
java -cp "out;lib\*" Main
pause
```
Then just double-click `run.bat` to start.

---

## Setup — Linux

### Steps

```bash
# 1. Install Java and MySQL
sudo apt update
sudo apt install openjdk-17-jdk mysql-server

# 2. Start MySQL and secure it
sudo systemctl start mysql
sudo mysql_secure_installation

# 3. Create database
mysql -u root -p < schema.sql

# 4. Download JDBC driver
mkdir -p lib
curl -L -o lib/mysql-connector-j.jar "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.1.0/mysql-connector-j-9.1.0.jar"

# 5. Set MySQL credentials: cp .env.example .env, then edit DB_PASSWORD

# 6. Build and run
chmod +x run.sh
./run.sh
```

---

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

```
When you log an attempt:
  SOLVED_EASILY    → interval = interval × 2  (e.g., 1 → 2 → 4 → 8 → 16 days)
  SOLVED_WITH_HELP → interval stays the same
  COULD_NOT_SOLVE  → interval resets to 1 day

next_review_date = today + new interval
```

New questions start with `interval = 1` (review tomorrow).

---

## Menu Options Explained

```
=== Menu ===
1. Add a question         → Enter title, URL, topic, difficulty, notes
2. List all questions     → Shows everything you've added
3. Log an attempt         → Pick a question, rate how it went → updates schedule
4. View today's reviews   → Shows problems due for review today (or overdue)
5. View stats by topic    → Table showing attempts and results per topic
6. Open question in browser → Opens the LeetCode link in your default browser
7. Exit
```

---

## Authentication / Multi-User

The app has a built-in **login and registration** system, so multiple people can use the same database with their own private data.

- On startup you choose **Login** or **Register**.
- Passwords are hashed with **SHA-256 + a unique random salt** (see `PasswordUtil.java`) — plain-text passwords are never stored.
- Every question, attempt, and review schedule is tied to a `user_id`, so you only ever see your own data.

### Already have an old single-user database?

If you built this before the user system existed, run the migration script to upgrade without losing data:

```bash
mysql -u root -p < migrate.sql
```

This creates the `users` table, adds a `user_id` column, and assigns your existing questions to a default account (username: `admin`, password: `admin`). Log in as `admin` to see your old data, then register your own account.

---

## Common Issues

### "Connection refused" or "Access denied"
- MySQL isn't running. Start it:
  - macOS: `brew services start mysql`
  - Windows: Check Services → MySQL is running
  - Linux: `sudo systemctl start mysql`
- Wrong password in `DatabaseHelper.java`

### "No suitable driver found"
- The MySQL connector JAR is missing from `lib/`
- Re-download it (see setup steps above)

### "Table doesn't exist"
- You haven't run `schema.sql` yet
- Run: `mysql -u root -p < schema.sql`

### Topics showing as duplicates (e.g., "Trees" vs "trees")
- The topic field is case-sensitive
- Be consistent with capitalization when adding questions
- Or change `DatabaseHelper.java` stats query to use `LOWER(q.topic)` for grouping

### App won't compile
- Make sure you have Java 17+: `java --version`
- Make sure all `.java` files are in the `src/` folder

---

## Making Changes (Without Kiro)

If you don't have Kiro available and want to make changes, here's what each file does and how to modify them:

### Want to change the menu?
Edit `src/Main.java`:
- `printMenu()` — what the user sees
- The `switch` block in `main()` — which method gets called
- Add new `private static void yourMethod()` for new features

### Want to add a new database query?
Edit `src/DatabaseHelper.java`:
- Add a new method following the same pattern (try-with-resources + PreparedStatement)
- Use `?` placeholders for user input (never concatenate strings into SQL)

### Want to change the spaced repetition logic?
Edit `src/SpacedRepetition.java`:
- It's one method with a switch statement
- Change the multipliers or add new result types

### Want to add a new column to a table?
1. Run ALTER TABLE in MySQL:
   ```sql
   ALTER TABLE questions ADD COLUMN new_column VARCHAR(100);
   ```
2. Update the matching POJO (`Question.java`) with the new field + getter/setter
3. Update `DatabaseHelper.java` INSERT and SELECT queries to include the new column
4. Update `schema.sql` so new installations have the column

### Want to add login/authentication?
See the [Authentication section](#authentication--multi-user) above for Option B steps.

### General tips for AI tools (ChatGPT, Claude, etc.)
When asking an AI to help you modify this project, paste:
1. This README (for context)
2. The specific file you want changed
3. What you want to add/change

Example prompt:
> "Here's my Main.java [paste]. I want to add a feature to delete a question by ID. Update the menu and add the method."

---

## Future Ideas (Phase 2+)

- [ ] Filter reviews by topic
- [ ] Delete/edit questions
- [ ] Export data to CSV
- [ ] Import problems from a file
- [ ] Web UI version
- [ ] Cloud database for cross-device sync
- [x] Login system for multi-user
- [ ] Difficulty-based review priority
