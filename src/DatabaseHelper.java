import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String URL = getEnv("DB_URL", "jdbc:mysql://localhost:3306/interview_prep_buddy");
    private static final String USER = getEnv("DB_USER", "root");
    private static final String PASSWORD = getEnv("DB_PASSWORD", "");

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ======================== USERS ========================

    public User register(String username, String password) {
        // Hash the password before storing
        String hashedPassword = PasswordUtil.hashPassword(password);

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                User user = new User(username, hashedPassword);
                user.setId(keys.getInt(1));
                return user;
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.err.println("Username already taken. Try a different one.");
            } else {
                System.err.println("Error registering: " + e.getMessage());
            }
        }
        return null;
    }

    public User login(String username, String password) {
        // Fetch user by username, then verify password hash
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password");
                if (PasswordUtil.verifyPassword(password, storedHash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    return user;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
        return null;
    }

    // ======================== QUESTIONS ========================

    public void addQuestion(Question question, int userId) {
        String sql = "INSERT INTO questions (user_id, title, url, topic, difficulty, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setString(2, question.getTitle());
            stmt.setString(3, question.getUrl());
            stmt.setString(4, question.getTopic());
            stmt.setString(5, question.getDifficulty());
            stmt.setString(6, question.getNotes());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                question.setId(keys.getInt(1));
            }

            // Create initial review schedule (review tomorrow)
            createInitialSchedule(question.getId());

        } catch (SQLException e) {
            System.err.println("Error adding question: " + e.getMessage());
        }
    }

    public List<Question> getAllQuestions(int userId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE user_id = ? ORDER BY id";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setTitle(rs.getString("title"));
                q.setUrl(rs.getString("url"));
                q.setTopic(rs.getString("topic"));
                q.setDifficulty(rs.getString("difficulty"));
                q.setNotes(rs.getString("notes"));
                questions.add(q);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching questions: " + e.getMessage());
        }
        return questions;
    }

    public Question getQuestionById(int id, int userId) {
        String sql = "SELECT * FROM questions WHERE id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setTitle(rs.getString("title"));
                q.setUrl(rs.getString("url"));
                q.setTopic(rs.getString("topic"));
                q.setDifficulty(rs.getString("difficulty"));
                q.setNotes(rs.getString("notes"));
                return q;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching question: " + e.getMessage());
        }
        return null;
    }

    // ======================== ATTEMPTS ========================

    public void logAttempt(Attempt attempt) {
        String sql = "INSERT INTO attempts (question_id, result, attempted_at) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attempt.getQuestionId());
            stmt.setString(2, attempt.getResult());
            stmt.setDate(3, Date.valueOf(attempt.getAttemptedAt()));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error logging attempt: " + e.getMessage());
        }
    }

    // ======================== REVIEW SCHEDULE ========================

    private void createInitialSchedule(int questionId) {
        String sql = "INSERT INTO review_schedule (question_id, next_review_date, interval_days) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            stmt.setDate(2, Date.valueOf(LocalDate.now().plusDays(1)));
            stmt.setInt(3, 1);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error creating schedule: " + e.getMessage());
        }
    }

    public ReviewSchedule getScheduleForQuestion(int questionId) {
        String sql = "SELECT * FROM review_schedule WHERE question_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                ReviewSchedule schedule = new ReviewSchedule();
                schedule.setId(rs.getInt("id"));
                schedule.setQuestionId(rs.getInt("question_id"));
                schedule.setNextReviewDate(rs.getDate("next_review_date").toLocalDate());
                schedule.setIntervalDays(rs.getInt("interval_days"));
                return schedule;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching schedule: " + e.getMessage());
        }
        return null;
    }

    public void updateSchedule(int questionId, LocalDate nextReviewDate, int intervalDays) {
        String sql = "UPDATE review_schedule SET next_review_date = ?, interval_days = ? WHERE question_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(nextReviewDate));
            stmt.setInt(2, intervalDays);
            stmt.setInt(3, questionId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating schedule: " + e.getMessage());
        }
    }

    public List<Question> getReviewsForToday(int userId) {
        List<Question> questions = new ArrayList<>();
        String sql = """
                SELECT q.* FROM questions q
                JOIN review_schedule rs ON q.id = rs.question_id
                WHERE rs.next_review_date <= ? AND q.user_id = ?
                ORDER BY rs.next_review_date
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setTitle(rs.getString("title"));
                q.setUrl(rs.getString("url"));
                q.setTopic(rs.getString("topic"));
                q.setDifficulty(rs.getString("difficulty"));
                q.setNotes(rs.getString("notes"));
                questions.add(q);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching today's reviews: " + e.getMessage());
        }
        return questions;
    }

    // ======================== STATS ========================

    public void printStatsByTopic(int userId) {
        String sql = """
                SELECT q.topic,
                       COUNT(DISTINCT q.id) AS total_questions,
                       COUNT(a.id) AS total_attempts,
                       SUM(CASE WHEN a.result = 'SOLVED_EASILY' THEN 1 ELSE 0 END) AS solved_easily,
                       SUM(CASE WHEN a.result = 'SOLVED_WITH_HELP' THEN 1 ELSE 0 END) AS solved_with_help,
                       SUM(CASE WHEN a.result = 'COULD_NOT_SOLVE' THEN 1 ELSE 0 END) AS could_not_solve
                FROM questions q
                LEFT JOIN attempts a ON q.id = a.question_id
                WHERE q.user_id = ?
                GROUP BY q.topic
                ORDER BY q.topic
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== Stats by Topic =====");
            System.out.printf("%-15s %-10s %-10s %-14s %-16s %-14s%n",
                    "Topic", "Questions", "Attempts", "Solved Easy", "Solved w/ Help", "Could Not Solve");
            System.out.println("-".repeat(80));

            while (rs.next()) {
                System.out.printf("%-15s %-10d %-10d %-14d %-16d %-14d%n",
                        rs.getString("topic"),
                        rs.getInt("total_questions"),
                        rs.getInt("total_attempts"),
                        rs.getInt("solved_easily"),
                        rs.getInt("solved_with_help"),
                        rs.getInt("could_not_solve"));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching stats: " + e.getMessage());
        }
    }
}
