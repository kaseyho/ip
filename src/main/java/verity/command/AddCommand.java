package verity.command;

import java.io.IOException;

import verity.task.Task;
import verity.task.TaskList;

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
     * @param context Command execution context.
     * @throws IOException If the task list cannot be saved.
     */
    @Override
    public void execute(CommandContext context)
            throws IOException {
        TaskList tasks = context.getTasks();

        tasks.add(task);
        context.getStorage().saveTasks(tasks);
        context.getUi().showTaskAdded(task, tasks.size());
    }
}
