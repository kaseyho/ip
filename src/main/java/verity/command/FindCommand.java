package verity.command;

import java.time.LocalDate;
import java.util.List;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a verity.command that finds tasks occurring on a date.
 */
public class FindCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a find verity.command.
     *
     * @param date Date to search for.
     */
    public FindCommand(LocalDate date) {
        super(false);
        this.date = date;
    }

    /**
     * Finds and displays tasks occurring on the date.
     *
     * @param tasks verity.task.Task list to search.
     * @param ui UI used to display matching tasks.
     * @param storage verity.storage.Storage, which is not used.
     */
    @Override
    public void execute(
            TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findOn(date);
        ui.showTasksOn(date, matchingTasks);
    }
}
