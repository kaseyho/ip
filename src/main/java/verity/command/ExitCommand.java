package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a verity.command that exits the chatbot.
 */
public class ExitCommand extends Command {

    /**
     * Creates an exit verity.command.
     */
    public ExitCommand() {
        super(true);
    }

    /**
     * Executes the exit verity.command.
     *
     * <p>No action is needed because verity.ui.Verity displays the exit message
     * after leaving its verity.command loop.</p>
     *
     * @param tasks verity.task.Task list, which is not used.
     * @param ui UI, which is not used.
     * @param storage verity.storage.Storage, which is not used.
     */
    @Override
    public void execute(
            TaskList tasks, Ui ui, Storage storage) {
        // Exiting is handled through isExit().
    }
}
