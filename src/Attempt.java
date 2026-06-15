import java.time.LocalDate;

public class Attempt {
    private int id;
    private int questionId;
    private String result; // SOLVED_EASILY, SOLVED_WITH_HELP, COULD_NOT_SOLVE
    private LocalDate attemptedAt;

    public Attempt() {}

    public Attempt(int questionId, String result, LocalDate attemptedAt) {
        this.questionId = questionId;
        this.result = result;
        this.attemptedAt = attemptedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public LocalDate getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDate attemptedAt) { this.attemptedAt = attemptedAt; }

    @Override
    public String toString() {
        return String.format("[%d] Question #%d - %s on %s", id, questionId, result, attemptedAt);
    }
}
