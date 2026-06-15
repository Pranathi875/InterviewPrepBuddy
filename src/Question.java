public class Question {
    private int id;
    private String title;
    private String url;
    private String topic;
    private String difficulty;
    private String notes;

    public Question() {}

    public Question(String title, String url, String topic, String difficulty, String notes) {
        this.title = title;
        this.url = url;
        this.topic = topic;
        this.difficulty = difficulty;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s, %s) - %s", id, title, topic, difficulty, url);
    }
}
