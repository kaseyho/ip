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
     * Returns Verity's exit message.
     *
     * @param tasks Task list, which is not used.
     * @param ui UI used to format the exit message.
     * @param storage Storage, which is not used.
     * @return Exit message.
     */
    @Override
    public String execute(
            TaskList tasks, Ui ui, Storage storage) {
        return ui.getExitMessage();
    }
}
