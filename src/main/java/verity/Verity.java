package verity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import verity.command.Command;
import verity.exception.VerityException;
import verity.parser.Parser;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Coordinates the chatbot's UI, storage, parser, and task list.
 */
public class Verity {
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;

    private TaskList tasks;
    private boolean isInitialized;
    private String initializationErrorMessage;

    /**
     * Creates a chatbot that stores its tasks at the specified path.
     *
     * @param dataFilePath Path of the task data file.
     */
    public Verity(Path dataFilePath) {
        this.ui = new Ui();
        this.storage = new Storage(dataFilePath);
        this.parser = new Parser();
        this.tasks = new TaskList();
        this.isInitialized = false;
        this.initializationErrorMessage = null;
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     */
    public void run() {
        System.out.println(ui.getGreeting());

        if (!initialize()) {
            System.out.println(initializationErrorMessage);
            return;
        }

        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command command =
                        parser.parse(fullCommand, tasks.size());
                String response =
                        command.execute(tasks, ui, storage);

                System.out.println(response);
                isExit = command.isExit();
            } catch (VerityException exception) {
                System.out.println(ui.getCommandErrorMessage(
                        exception.getMessage()));
            } catch (IOException exception) {
                System.out.println(ui.getSavingErrorMessage());
                return;
            }
        }
    }

    /**
     * Returns Verity's response to one user command.
     *
     * @param input User command to process.
     * @return Verity's response.
     */
    public String getResponse(String input) {
        if (!initialize()) {
            return initializationErrorMessage;
        }

        Command command;
        try {
            command = parser.parse(input, tasks.size());
        } catch (VerityException exception) {
            return ui.getCommandErrorMessage(exception.getMessage());
        }

        List<String> taskSnapshot = tasks.getTasks().stream()
                .map(task -> task.serialize())
                .toList();
        try {
            return command.execute(tasks, ui, storage);
        } catch (IOException exception) {
            restoreTasks(taskSnapshot);
            return ui.getSavingErrorMessage();
        }
    }

    /**
     * Restores the in-memory task list after a command fails to save.
     *
     * @param taskSnapshot Serialized tasks from before command execution.
     */
    private void restoreTasks(List<String> taskSnapshot) {
        try {
            tasks = new TaskList(parser.parseSavedTasks(taskSnapshot));
        } catch (VerityException exception) {
            throw new IllegalStateException(
                    "Could not restore the task list.", exception);
        }
    }

    /**
     * Loads saved tasks the first time Verity is used.
     *
     * @return True if initialization succeeded.
     */
    private boolean initialize() {
        if (isInitialized) {
            return initializationErrorMessage == null;
        }

        isInitialized = true;
        try {
            List<String> savedTaskLines = storage.loadTaskLines();
            tasks = new TaskList(
                    parser.parseSavedTasks(savedTaskLines));
            return true;
        } catch (IOException exception) {
            initializationErrorMessage = ui.getLoadingErrorMessage();
            return false;
        } catch (VerityException exception) {
            initializationErrorMessage =
                    ui.getCorruptedDataErrorMessage(
                            exception.getMessage());
            return false;
        }
    }

    /**
     * Starts Verity using the default data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Verity(Path.of("data", "verity.txt")).run();
    }
}
