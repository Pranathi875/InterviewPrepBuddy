import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final DatabaseHelper db = new DatabaseHelper();
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser;

    public static void main(String[] args) {
        System.out.println("=== Interview Prep Buddy ===");
        System.out.println("Spaced repetition for DSA practice\n");

        // Login or Register
        currentUser = loginOrRegister();
        if (currentUser == null) {
            System.out.println("Could not log in. Exiting.");
            return;
        }
        System.out.println("\nWelcome, " + currentUser.getUsername() + "!\n");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addQuestion();
                case "2" -> listAllQuestions();
                case "3" -> logAttempt();
                case "4" -> viewTodaysReviews();
                case "5" -> viewStatsByTopic();
                case "6" -> openQuestionInBrowser();
                case "7" -> {
                    System.out.println("Goodbye, " + currentUser.getUsername() + "! Keep grinding.");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    private static User loginOrRegister() {
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Register (new user)");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();

            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            if (username.isEmpty() || username.length() < 3) {
                System.out.println("Username must be at least 3 characters.\n");
                continue;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            if (password.isEmpty() || password.length() < 4) {
                System.out.println("Password must be at least 4 characters.\n");
                continue;
            }

            if (choice.equals("1")) {
                User user = db.login(username, password);
                if (user != null) {
                    return user;
                }
                System.out.println("Invalid username or password. Try again.\n");
            } else if (choice.equals("2")) {
                User user = db.register(username, password);
                if (user != null) {
                    System.out.println("✓ Registered successfully!");
                    return user;
                }
                System.out.println("Registration failed. Try again.\n");
            } else {
                System.out.println("Invalid choice.\n");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== Menu ===");
        System.out.println("1. Add a question");
        System.out.println("2. List all questions");
        System.out.println("3. Log an attempt");
        System.out.println("4. View today's reviews");
        System.out.println("5. View stats by topic");
        System.out.println("6. Open question in browser");
        System.out.println("7. Exit");
        System.out.print("Choose: ");
    }

    private static void addQuestion() {
        System.out.print("Title (e.g., Two Sum): ");
        String title = scanner.nextLine().trim();

        System.out.print("URL (LeetCode link): ");
        String url = scanner.nextLine().trim();

        System.out.print("Topic (e.g., Array, Stack, LinkedList): ");
        String topic = scanner.nextLine().trim();

        System.out.print("Difficulty (EASY, MEDIUM, HARD): ");
        String difficulty = scanner.nextLine().trim().toUpperCase();

        System.out.print("Notes (your approach, optional): ");
        String notes = scanner.nextLine().trim();

        Question question = new Question(title, url, topic, difficulty, notes);
        db.addQuestion(question, currentUser.getId());
        System.out.println("✓ Added: " + question.getTitle() + " (ID: " + question.getId() + ")");
    }

    private static void listAllQuestions() {
        List<Question> questions = db.getAllQuestions(currentUser.getId());
        if (questions.isEmpty()) {
            System.out.println("No questions yet. Add some!");
            return;
        }
        System.out.println("\n===== All Questions =====");
        for (Question q : questions) {
            System.out.println(q);
        }
    }

    private static void logAttempt() {
        List<Question> questions = db.getAllQuestions(currentUser.getId());
        if (questions.isEmpty()) {
            System.out.println("No questions to attempt. Add some first!");
            return;
        }

        System.out.println("\nYour questions:");
        for (Question q : questions) {
            System.out.printf("  [%d] %s (%s)%n", q.getId(), q.getTitle(), q.getTopic());
        }

        System.out.print("Enter question ID: ");
        int questionId;
        try {
            questionId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        // Verify question exists AND belongs to this user
        Question question = db.getQuestionById(questionId, currentUser.getId());
        if (question == null) {
            System.out.println("Question not found.");
            return;
        }

        System.out.println("How did it go?");
        System.out.println("  1. SOLVED_EASILY");
        System.out.println("  2. SOLVED_WITH_HELP");
        System.out.println("  3. COULD_NOT_SOLVE");
        System.out.print("Choose (1-3): ");
        String resultChoice = scanner.nextLine().trim();

        String result = switch (resultChoice) {
            case "1" -> "SOLVED_EASILY";
            case "2" -> "SOLVED_WITH_HELP";
            case "3" -> "COULD_NOT_SOLVE";
            default -> {
                System.out.println("Invalid choice.");
                yield null;
            }
        };

        if (result == null) return;

        Attempt attempt = new Attempt(questionId, result, LocalDate.now());
        db.logAttempt(attempt);

        ReviewSchedule schedule = db.getScheduleForQuestion(questionId);
        if (schedule != null) {
            int newInterval = SpacedRepetition.calculateNextInterval(result, schedule.getIntervalDays());
            LocalDate nextReview = LocalDate.now().plusDays(newInterval);
            db.updateSchedule(questionId, nextReview, newInterval);
            System.out.printf("✓ Logged! Next review for \"%s\": %s (%d days)%n",
                    question.getTitle(), nextReview, newInterval);
        } else {
            System.out.println("✓ Attempt logged.");
        }
    }

    private static void viewTodaysReviews() {
        List<Question> reviews = db.getReviewsForToday(currentUser.getId());
        if (reviews.isEmpty()) {
            System.out.println("🎉 No reviews due today! You're all caught up.");
            return;
        }
        System.out.println("\n===== Today's Reviews =====");
        System.out.printf("You have %d question(s) to review:%n%n", reviews.size());
        for (Question q : reviews) {
            System.out.printf("  [%d] %s (%s, %s)%n      %s%n",
                    q.getId(), q.getTitle(), q.getTopic(), q.getDifficulty(), q.getUrl());
        }
    }

    private static void viewStatsByTopic() {
        db.printStatsByTopic(currentUser.getId());
    }

    private static void openQuestionInBrowser() {
        List<Question> questions = db.getAllQuestions(currentUser.getId());
        if (questions.isEmpty()) {
            System.out.println("No questions yet. Add some first!");
            return;
        }

        System.out.println("\nYour questions:");
        for (Question q : questions) {
            System.out.printf("  [%d] %s (%s)%n", q.getId(), q.getTitle(), q.getTopic());
        }

        System.out.print("Enter question ID to open: ");
        int questionId;
        try {
            questionId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        Question question = db.getQuestionById(questionId, currentUser.getId());
        if (question == null) {
            System.out.println("Question not found.");
            return;
        }

        String url = question.getUrl();
        if (url == null || url.isBlank()) {
            System.out.println("No URL saved for this question.");
            return;
        }

        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", url});
            } else if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", url});
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            }
            System.out.println("✓ Opening: " + url);
        } catch (Exception e) {
            System.out.println("Couldn't open browser. Here's the link:");
            System.out.println("  " + url);
        }
    }
}
