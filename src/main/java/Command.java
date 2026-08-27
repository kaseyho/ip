import java.io.IOException;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    private final boolean isExit;

    /**
     * Creates a command.
     *
     * @param isExit Whether this command exits the chatbot.
     */
    protected Command(boolean isExit) {
        this.isExit = isExit;
    }

    /**
     * Executes the command.
     *
     * @param tasks Task list to operate on.
     * @param ui UI used to display feedback.
     * @param storage Storage used to save changes.
     * @throws IOException If task changes cannot be saved.
     */
    public abstract void execute(
            TaskList tasks, Ui ui, Storage storage)
            throws IOException;

    /**
     * Returns whether this command exits the chatbot.
     *
     * @return True if this is an exit command.
     */
    public boolean isExit() {
        return isExit;
    }
}