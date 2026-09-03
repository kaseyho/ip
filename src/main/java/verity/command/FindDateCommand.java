package verity.command;

import java.time.LocalDate;
import java.util.List;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a command that finds tasks occurring on a date.
 */
public class FindDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a date-search command.
     *
     * @param date Date to search for.
     */
    public FindDateCommand(LocalDate date) {
        super(false);
        this.date = date;
    }

    /**
     * Finds and returns tasks occurring on the date.
     *
     * @param tasks Task list to search.
     * @param ui UI used to format matching tasks.
     * @param storage Storage, which is not used.
     * @return User-facing response after execution.
     */
    @Override
    public String execute(
            TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findOn(date);
        return ui.getTasksOnMessage(date, matchingTasks);
    }
}
