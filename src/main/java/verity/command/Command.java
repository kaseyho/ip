package verity.command;

import java.io.IOException;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents an executable user verity.command.
 */
public abstract class Command {
    private final boolean isExit;

    /**
     * Creates a verity.command.
     *
     * @param isExit Whether this verity.command exits the chatbot.
     */
    protected Command(boolean isExit) {
        this.isExit = isExit;
    }

    /**
     * Executes the verity.command.
     *
     * @param tasks verity.task.Task list to operate on.
     * @param ui UI used to display feedback.
     * @param storage verity.storage.Storage used to save changes.
     * @throws IOException If task changes cannot be saved.
     */
    public abstract void execute(
            TaskList tasks, Ui ui, Storage storage)
            throws IOException;

    /**
     * Returns whether this verity.command exits the chatbot.
     *
     * @return True if this is an exit verity.command.
     */
    public boolean isExit() {
        return isExit;
    }
}
