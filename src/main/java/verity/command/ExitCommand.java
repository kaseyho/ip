package verity.command;

/**
 * Represents a command that exits the chatbot.
 */
public class ExitCommand extends Command {

    /**
     * Creates an exit command.
     */
    public ExitCommand() {
        super(true);
    }

    /**
     * Performs no action because exit handling occurs in the command loop.
     *
     * @param context Command execution context, which is not used.
     */
    @Override
    public void execute(CommandContext context) {
    }
}
