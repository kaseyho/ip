import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
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

        while (true) {
            String command = ui.readCommand();
            String[] commandParts =
                    command.trim().split("\\s+");

            try {
                Parser.Command commandType =
                        parser.parseCommand(commandParts[0]);

                if (commandType == Parser.Command.BYE) {
                    break;
                } else if (commandType == Parser.Command.MARK) {
                    int taskNumber = parser.parseTaskNumber(
                            commandParts, tasks.size());
                    Task markedTask = tasks.mark(taskNumber);
                    storage.saveTasks(tasks);
                    ui.showTaskMarked(markedTask);
                } else if (commandType
                        == Parser.Command.UNMARK) {
                    int taskNumber = parser.parseTaskNumber(
                            commandParts, tasks.size());
                    Task unmarkedTask = tasks.unmark(taskNumber);
                    storage.saveTasks(tasks);
                    ui.showTaskUnmarked(unmarkedTask);
                } else if (commandType == Parser.Command.LIST) {
                    ui.showTaskList(tasks);
                } else if (commandType
                        == Parser.Command.DELETE) {
                    int taskNumber = parser.parseTaskNumber(
                            commandParts, tasks.size());
                    Task removedTask = tasks.delete(taskNumber);
                    storage.saveTasks(tasks);
                    ui.showTaskDeleted(
                            removedTask, tasks.size());
                } else if (commandType == Parser.Command.TODO) {
                    Todo todoTask =
                            parser.parseTodo(commandParts);
                    tasks.add(todoTask);
                    storage.saveTasks(tasks);
                    ui.showTaskAdded(todoTask, tasks.size());
                } else if (commandType
                        == Parser.Command.DEADLINE) {
                    Deadline deadlineTask =
                            parser.parseDeadline(commandParts);
                    tasks.add(deadlineTask);
                    storage.saveTasks(tasks);
                    ui.showTaskAdded(
                            deadlineTask, tasks.size());
                } else if (commandType
                        == Parser.Command.EVENT) {
                    Event eventTask =
                            parser.parseEvent(commandParts);
                    tasks.add(eventTask);
                    storage.saveTasks(tasks);
                    ui.showTaskAdded(eventTask, tasks.size());
                } else if (commandType == Parser.Command.FIND) {
                    LocalDate date =
                            parser.parseFindDate(commandParts);
                    List<Task> matchingTasks =
                            tasks.findOn(date);
                    ui.showTasksOn(date, matchingTasks);
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