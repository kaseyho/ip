package verity.command;

import java.io.IOException;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a command that marks a task as incomplete.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates an unmark command.
     *
     * @param taskIndex Zero-based index of the task.
     */
    public UnmarkCommand(int taskIndex) {
        super(false);
        this.taskIndex = taskIndex;
    }

    /**
     * Unmarks the task and saves the updated task list.
     *
     * @param tasks Task list to update.
     * @param ui UI used to display feedback.
     * @param storage Storage used to save the task list.
     * @throws IOException If the task list cannot be saved.
     */
    @Override
    public void execute(CommandContext context)
            throws IOException {
        TaskList tasks = context.getTasks();

        Task unmarkedTask = tasks.unmark(taskIndex);
        context.getStorage().saveTasks(tasks);
        context.getUi().showTaskUnmarked(unmarkedTask);
    }
}
