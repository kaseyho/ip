package verity.command;


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
     * @param context Command execution context.
     */
    @Override
    public void execute(CommandContext context) {
        context.getUi().showTaskList(context.getTasks());
    }
}
