import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate by;

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

    @Override
    public String serialize() {
        return String.join("\t", "D", getStorageStatus(), description,
                by.toString());
    }
}
