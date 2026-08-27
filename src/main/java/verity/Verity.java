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
    }

    /**
     * Starts the chatbot and processes commands until the user exits.
     */
    public void run() {
        ui.showGreeting();

        if (!loadTasks()) {
            return;
        }

        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command command =
                        parser.parse(fullCommand, tasks.size());
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (VerityException exception) {
                ui.showCommandError(exception.getMessage());
            } catch (IOException exception) {
                ui.showSavingError();
                return;
            }
        }

        ui.showExit();
    }
    /**
     * Loads saved tasks into the task list.
     *
     * @return True if loading succeeded.
     */
    private boolean loadTasks() {
        try {
            List<String> savedTaskLines =
                    storage.loadTaskLines();
            tasks = new TaskList(
                    parser.parseSavedTasks(savedTaskLines));
            return true;
        } catch (IOException exception) {
            ui.showLoadingError();
            return false;
        } catch (VerityException exception) {
            ui.showCorruptedDataError(exception.getMessage());
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
