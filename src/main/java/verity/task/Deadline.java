package verity.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Description of the task.
     * @param by Due date of the task.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    public LocalDate getBy() {
        return by;
    }

    @Override
    public String getStatus() {
        return "[D]" + super.getStatus()
                + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns a line representing this deadline in the data file.
     *
     * @return Tab-separated deadline data.
     */
    @Override
    public String serialize() {
        return String.join("\t", "D", getStorageStatus(), description,
                by.toString());
    }

    /**
     * Returns whether this deadline is due on the specified date.
     *
     * @param date Date to check.
     * @return True if the deadline is due on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }
}
