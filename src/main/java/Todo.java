public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getStatus() {
        return "[T]" + super.getStatus();
    }

    @Override
    public String serialize() {
        return String.join("\t", "T", getStorageStatus(), description);
    }
}
