import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class Verity {

    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage =
                new Storage(Path.of("data", "verity.txt"));
        Parser parser = new Parser();

        ui.showGreeting();

        TaskList tasks;
        try {
            List<String> savedTaskLines =
                    storage.loadTaskLines();
            tasks = new TaskList(
                    parser.parseSavedTasks(savedTaskLines));
        } catch (IOException exception) {
            ui.showLoadingError();
            return;
        } catch (VerityException exception) {
            ui.showCorruptedDataError(exception.getMessage());
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
}