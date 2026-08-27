/**
 * Represents a command that displays all tasks.
 */
public class ListCommand extends Command {

    /**
     * Creates a list command.
     */
    public ListCommand() {
        super(false);
    }

    /**
     * Displays all tasks.
     *
     * @param tasks Task list to display.
     * @param ui UI used to display the task list.
     * @param storage Storage, which is not used.
     */
    @Override
    public void execute(
            TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}