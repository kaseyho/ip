package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a command that displays all tasks.
 */
public class ListCommand extends Command {

    /**
     * Creates a list command.
     */
    public ListCommand() {
        super(false);
    }

    /**
     * Returns all tasks.
     *
     * @param tasks Task list to display.
     * @param ui UI used to format the task list.
     * @param storage Storage, which is not used.
     * @return User-facing response after execution.
     */
    @Override
    public String execute(
            TaskList tasks, Ui ui, Storage storage) {
        return ui.getTaskListMessage(tasks);
    }
}
