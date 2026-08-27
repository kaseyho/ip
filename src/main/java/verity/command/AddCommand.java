package verity.command;

import java.io.IOException;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a command that adds a task.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        super(false);
        this.task = task;
    }

    /**
     * Adds the task, saves the updated task list, and displays feedback.
     *
     * @param tasks Task list to update.
     * @param ui UI used to display feedback.
     * @param storage Storage used to save the task list.
     * @throws IOException If the task list cannot be saved.
     */
    @Override
    public void execute(
            TaskList tasks, Ui ui, Storage storage)
            throws IOException {
        tasks.add(task);
        storage.saveTasks(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
