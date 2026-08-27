package verity.command;

import java.io.IOException;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a command that deletes a task.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a delete command.
     *
     * @param taskIndex Zero-based index of the task.
     */
    public DeleteCommand(int taskIndex) {
        super(false);
        this.taskIndex = taskIndex;
    }

    /**
     * Deletes the task and saves the updated task list.
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
        Task deletedTask = tasks.delete(taskIndex);
        storage.saveTasks(tasks);
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
