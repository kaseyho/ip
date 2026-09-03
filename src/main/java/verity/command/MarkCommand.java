package verity.command;

import java.io.IOException;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a command that marks a task as completed.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a mark command.
     *
     * @param taskIndex Zero-based index of the task.
     */
    public MarkCommand(int taskIndex) {
        super(false);
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the task and saves the updated task list.
     *
     * @param tasks Task list to update.
     * @param ui UI used to format feedback.
     * @param storage Storage used to save the task list.
     * @return User-facing response after execution.
     * @throws IOException If the task list cannot be saved.
     */
    @Override
    public String execute(
            TaskList tasks, Ui ui, Storage storage)
            throws IOException {
        Task markedTask = tasks.mark(taskIndex);
        storage.saveTasks(tasks);
        return ui.getTaskMarkedMessage(markedTask);
    }
}
