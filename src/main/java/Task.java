/**
 * Represents a task that can be tracked and stored.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatus() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }

    /**
     *  Returns a line representing this task in the data file.
     *
     * @return Serialized task data.
     */
    public abstract String serialize();

    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsUndone() {
        this.isDone = false;
    }
}
