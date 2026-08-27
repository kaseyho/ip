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

    public Event(String description, LocalDate fromDate, LocalDate toDate) {
        super(description);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public String getStatus() {
        return "[E]" + super.getStatus()
                + " (from: " + fromDate.format(DISPLAY_DATE_FORMAT)
                + " to: " + toDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String serialize() {
        return String.join("\t", "E", getStorageStatus(), description,
                fromDate.toString(), toDate.toString());
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(fromDate) && !date.isAfter(toDate);
    }
}
