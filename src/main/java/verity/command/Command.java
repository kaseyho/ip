package verity.command;

import java.io.IOException;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    private final boolean isExit;

    /**
     * Creates a command.
     *
     * @param isExit Whether this command exits the chatbot.
     */
    protected Command(boolean isExit) {
        this.isExit = isExit;
    }

    /**
     * Executes the command.
     *
     * @param context Command execution context.
     * @throws IOException If task changes cannot be saved.
     */
    public abstract void execute(CommandContext context)
            throws IOException;

    /**
     * Returns whether this command exits the chatbot.
     *
     * @return True if this is an exit command.
     */
    public boolean isExit() {
        return isExit;
    }
}
