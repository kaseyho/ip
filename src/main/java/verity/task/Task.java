package verity.task;

import java.time.LocalDate;
import java.util.Locale;

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
     *  Returns a line representing this task in the data file.
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

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsUndone() {
        this.isDone = false;
    }
}
