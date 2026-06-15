import java.time.LocalDate;

public class ReviewSchedule {
    private int id;
    private int questionId;
    private LocalDate nextReviewDate;
    private int intervalDays;

    public ReviewSchedule() {}

    public ReviewSchedule(int questionId, LocalDate nextReviewDate, int intervalDays) {
        this.questionId = questionId;
        this.nextReviewDate = nextReviewDate;
        this.intervalDays = intervalDays;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public LocalDate getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDate nextReviewDate) { this.nextReviewDate = nextReviewDate; }

    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }

    @Override
    public String toString() {
        return String.format("Question #%d - Next review: %s (interval: %d days)",
                questionId, nextReviewDate, intervalDays);
    }
}
