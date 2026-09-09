package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Provides the dependencies needed to execute commands.
 */
public class CommandContext {
    private final TaskList tasks;
    private final Ui ui;
    private final Storage storage;

    /**
     * Creates a command execution context.
     *
     * @param tasks Task list on which commands operate.
     * @param ui UI used to display command results.
     * @param storage Storage used to save task changes.
     */
    public CommandContext(
            TaskList tasks, Ui ui, Storage storage) {
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
    }

    public TaskList getTasks() {
        return tasks;
    }

    public Ui getUi() {
        return ui;
    }

    public Storage getStorage() {
        return storage;
    }

}