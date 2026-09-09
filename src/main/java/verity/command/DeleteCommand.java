package verity.command;

import java.io.IOException;

import verity.task.Task;
import verity.task.TaskList;

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
     * @param context Command execution context.
     * @throws IOException If the task list cannot be saved.
     */
    @Override
    public void execute(CommandContext context)
            throws IOException {
        TaskList tasks = context.getTasks();

        Task deletedTask = tasks.delete(taskIndex);
        context.getStorage().saveTasks(tasks);
        context.getUi().showTaskDeleted(deletedTask, tasks.size());
    }
}
