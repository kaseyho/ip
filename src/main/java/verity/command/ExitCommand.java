package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a command that exits the chatbot.
 */
public class ExitCommand extends Command {

    /**
     * Creates an exit command.
     */
    public ExitCommand() {
        super(true);
    }

    /**
     * Executes the exit command.
     *
     * <p>No action is needed because {@link verity.Verity} displays the exit
     * message after leaving its command loop.</p>
     *
     * @param tasks Task list, which is not used.
     * @param ui UI, which is not used.
     * @param storage Storage, which is not used.
     */
    @Override
    public void execute(
            TaskList tasks, Ui ui, Storage storage) {
        // Exiting is handled through isExit().
    }
}
