public class Event extends Task {
    protected String fromDate;
    protected String toDate;

    public Event(String description, String fromDate, String toDate) {
        super(description);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public String getStatus() {
        return "[E]" + super.getStatus() + " (from: " + fromDate + " to: " + toDate + ")";
    }

    @Override
    public String serialize() {
        return String.join(
                "\t", "E", getStorageStatus(), description, fromDate, toDate);
    }

}
