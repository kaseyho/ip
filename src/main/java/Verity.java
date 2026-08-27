import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
            String command = ui.readCommand();
            String[] commandParts =
                    command.trim().split("\\s+");

            try {
                Parser.CommandType commandType =
                        parser.parseCommandType(commandParts[0]);

                if (commandType == Parser.CommandType.BYE) {
                    Command exitCommand = new ExitCommand();
                    exitCommand.execute(tasks, ui, storage);
                    isExit = exitCommand.isExit();
                } else if (commandType == Parser.CommandType.MARK) {
                    int taskIndex = parser.parseTaskNumber(
                            commandParts, tasks.size());
                    Command markCommand = new MarkCommand(taskIndex);
                    markCommand.execute(tasks, ui, storage);
                    isExit = markCommand.isExit();
                } else if (commandType == Parser.CommandType.UNMARK) {
                    int taskIndex = parser.parseTaskNumber(
                            commandParts, tasks.size());
                    Command unmarkCommand =
                            new UnmarkCommand(taskIndex);
                    unmarkCommand.execute(tasks, ui, storage);
                    isExit = unmarkCommand.isExit();
                } else if (commandType == Parser.CommandType.LIST) {
                    Command listCommand = new ListCommand();
                    listCommand.execute(tasks, ui, storage);
                    isExit = listCommand.isExit();
                } else if (commandType == Parser.CommandType.DELETE) {
                    int taskIndex = parser.parseTaskNumber(
                            commandParts, tasks.size());
                    Command deleteCommand =
                            new DeleteCommand(taskIndex);
                    deleteCommand.execute(tasks, ui, storage);
                    isExit = deleteCommand.isExit();
                } else if (commandType == Parser.CommandType.TODO) {
                    Todo todoTask =
                            parser.parseTodo(commandParts);
                    tasks.add(todoTask);
                    storage.saveTasks(tasks);
                    ui.showTaskAdded(todoTask, tasks.size());
                } else if (commandType
                        == Parser.CommandType.DEADLINE) {
                    Deadline deadlineTask =
                            parser.parseDeadline(commandParts);
                    tasks.add(deadlineTask);
                    storage.saveTasks(tasks);
                    ui.showTaskAdded(
                            deadlineTask, tasks.size());
                } else if (commandType
                        == Parser.CommandType.EVENT) {
                    Event eventTask =
                            parser.parseEvent(commandParts);
                    tasks.add(eventTask);
                    storage.saveTasks(tasks);
                    ui.showTaskAdded(eventTask, tasks.size());
                } else if (commandType == Parser.CommandType.FIND) {
                    Command findCommand = new FindCommand(
                            parser.parseFindDate(commandParts));
                    findCommand.execute(tasks, ui, storage);
                    isExit = findCommand.isExit();
                } else {
                    throw new VerityException(
                            "Start with todo, deadline or event.");
                }
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