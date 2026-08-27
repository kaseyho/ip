package verity.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event that occurs over a date range.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate fromDate;
    private final LocalDate toDate;

    /**
     * Creates an incomplete event task over the specified inclusive date range.
     *
     * @param description Description of the event.
     * @param fromDate First date of the event.
     * @param toDate Last date of the event.
     */
    public Event(String description, LocalDate fromDate, LocalDate toDate) {
        super(description);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    /**
     * Returns a user-facing summary of this event, including its date range.
     *
     * @return Formatted event summary.
     */
    @Override
    public String getStatus() {
        return "[E]" + super.getStatus()
                + " (from: " + fromDate.format(DISPLAY_DATE_FORMAT)
                + " to: " + toDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns a line representing this event in the data file.
     *
     * @return Tab-separated event data.
     */
    @Override
    public String serialize() {
        return String.join("\t", "E", getStorageStatus(), description,
                fromDate.toString(), toDate.toString());
    }

    /**
     * Returns whether this event occurs on the specified date.
     *
     * @param date Date to check.
     * @return True if the date falls within the event's inclusive date range.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }
}
