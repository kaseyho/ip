package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a verity.command that displays all tasks.
 */
public class ListCommand extends Command {

    /**
     * Creates a list verity.command.
     */
    public ListCommand() {
        super(false);
    }

    /**
     * Displays all tasks.
     *
     * @param tasks verity.task.Task list to display.
     * @param ui UI used to display the task list.
     * @param storage verity.storage.Storage, which is not used.
     */
    @Override
    public void execute(
            TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
