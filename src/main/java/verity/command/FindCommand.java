package verity.command;

import java.time.LocalDate;
import java.util.List;

import verity.task.Task;

/**
 * Represents a command that finds tasks occurring on a date.
 */
public class FindCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a find command.
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
     * @param context Command execution context.
     */
    @Override
    public void execute(CommandContext context) {
        List<Task> matchingTasks =
                context.getTasks().findOn(date);

        context.getUi().showTasksOn(date, matchingTasks);
    }
}
