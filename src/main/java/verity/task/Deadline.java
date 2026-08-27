package verity.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that is due on a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the specified description and due date.
     *
     * @param description Description of the deadline.
     * @param dueDate Date on which the deadline is due.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    public LocalDate getBy() {
        return dueDate;
    }

    @Override
    public String getStatus() {
        return "[D]" + super.getStatus()
                + " (by: " + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String serialize() {
        return String.join("\t", "D", getStorageStatus(), description,
                dueDate.toString());
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return dueDate.equals(date);
    }
}
