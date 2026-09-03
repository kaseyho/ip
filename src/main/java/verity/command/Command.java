package verity.command;

import java.io.IOException;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

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
     * Executes the command and returns its user-facing response.
     *
     * @param tasks Task list to operate on.
     * @param ui UI used to format feedback.
     * @param storage Storage used to save changes.
     * @return User-facing response after execution.
     * @throws IOException If task changes cannot be saved.
     */
    public abstract String execute(
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
