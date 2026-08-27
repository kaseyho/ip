package verity.task;

import java.time.LocalDate;

/**
 * Represents a task without an associated date.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the specified description.
     *
     * @param description Description of the todo.
     */
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

    @Override
    public boolean occursOn(LocalDate date) {
        return false;
    }
}
