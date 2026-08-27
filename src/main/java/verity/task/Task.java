package verity.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a task that can be tracked and stored.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns a user-facing summary of this task.
     * The summary includes its completion status and description.
     *
     * @return Formatted task summary.
     */
    public String getStatus() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }

    /**
     * Returns whether this task's description contains the keyword.
     * Matching is case-insensitive.
     *
     * @param keyword Keyword to search for.
     * @return True if the description contains the keyword.
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns a line representing this task in the data file.
     *
     * @return Serialized task data.
     */
    public abstract String serialize();

    /**
     * Returns whether this task occurs on the specified date.
     *
     * @param date Date to check.
     * @return True if this task occurs on the date.
     */
    public abstract boolean occursOn(LocalDate date);

    protected String getStorageStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markAsUndone() {
        this.isDone = false;
    }
}
