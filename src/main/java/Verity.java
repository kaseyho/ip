import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Verity {
    public static final String horizLine = "____________________________________________________________\n";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Storage storage = new Storage(Path.of("data", "verity.txt"));
        Parser parser = new Parser();

        String greeting = getGreeting();
        System.out.println(greeting);

        ArrayList<Task> tasks;
        try {
            List<String> savedTaskLines = storage.loadTaskLines();
            tasks = parser.parseSavedTasks(savedTaskLines);
        } catch (IOException exception) {
            System.out.println(horizLine
                    + "     I could not load your saved tasks.\n"
                    + "     Please check the data file and try again.\n"
                    + horizLine);
            return;
        } catch (VerityException exception) {
            System.out.println(horizLine
                    + "     The saved task data is corrupted.\n"
                    + "     " + exception.getMessage() + "\n"
                    + horizLine);
            return;
        }
        while (true) {
            String command = scanner.nextLine();
            String[] commandParts = command.trim().split("\\s+");
            try {
                Parser.Command commandType = parser.parseCommand(commandParts[0]);
                if (commandType == Parser.Command.BYE) {
                    break;
                } else if (commandType == Parser.Command.MARK) {
                    int taskNumber = parser.parseTaskNumber(commandParts, tasks.size());
                    tasks.get(taskNumber).markAsDone();
                    storage.saveTasks(tasks);
                    System.out.println(horizLine);
                    System.out.println("Nice! I've marked this task as done:\n");
                    System.out.println(tasks.get(taskNumber).getStatus() + "\n" + horizLine);
                } else if (commandType == Parser.Command.UNMARK) {
                    int taskNumber = parser.parseTaskNumber(commandParts, tasks.size());
                    tasks.get(taskNumber).markAsUndone();
                    storage.saveTasks(tasks);
                    System.out.println(horizLine);
                    System.out.println("Ok, I've marked this task as not done yet:\n");
                    System.out.println(tasks.get(taskNumber).getStatus() + "\n" + horizLine);

                } else if (commandType == Parser.Command.LIST) {
                    System.out.println(horizLine);
                    System.out.println("    Here are the tasks in your list:\n");
                    for (int i = 0; i < tasks.size(); ++i) {
                        System.out.println("    " + (i + 1) + "." + tasks.get(i).getStatus());
                    }
                    System.out.println(horizLine);
                } else if (commandType == Parser.Command.DELETE) {
                    int taskNumber = parser.parseTaskNumber(commandParts, tasks.size());
                    Task removedTask = tasks.remove(taskNumber);
                    storage.saveTasks(tasks);
                    System.out.println(horizLine);
                    System.out.println("Noted. I've removed this task:\n");
                    System.out.println(removedTask.getStatus() + "\n" + horizLine);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.\n");
                    System.out.println(horizLine);
                } else if (commandType == Parser.Command.TODO) {
                    Todo todoTask = parser.parseTodo(commandParts);
                    tasks.add(todoTask);
                    storage.saveTasks(tasks);
                    printAddedMessage(todoTask, tasks.size());
                } else if (commandType == Parser.Command.DEADLINE) {
                    Deadline deadlineTask = parser.parseDeadline(commandParts);
                    tasks.add(deadlineTask);
                    storage.saveTasks(tasks);
                    printAddedMessage(deadlineTask, tasks.size());
                } else if (commandType == Parser.Command.EVENT) {
                    Event eventTask = parser.parseEvent(commandParts);
                    tasks.add(eventTask);
                    storage.saveTasks(tasks);
                    printAddedMessage(eventTask, tasks.size());
                } else if (commandType == Parser.Command.FIND) {
                    LocalDate date = parser.parseFindDate(commandParts);
                    printTasksOn(date, tasks);
                } else {
                    throw new VerityException("Start with todo, deadline or event.");
                }
            } catch (VerityException e) {
                System.out.println(horizLine
                        + "     Speak your truth. " + e.getMessage() + "\n"
                        + horizLine);
            } catch (IOException e) {
                System.out.println(horizLine
                        + "     I could not save your tasks.\n"
                        + "     Please check the data folder and try again.\n"
                        + horizLine);
                return;
            }
        }
        String exitStr = getExitString();
        System.out.println(exitStr);
    }

    private static void printAddedMessage(Task task, int taskCount) {
        System.out.println(horizLine
                + "     Got it. I've added this task:\n"
                + "       " + task.getStatus() + "\n"
                + "     Now you have " + taskCount + " tasks in the list.\n"
                + horizLine);
    }

    private static void printTasksOn(LocalDate date, List<Task> tasks) {
        System.out.println(horizLine);
        System.out.println("    Tasks on " + date + ":\n");

        boolean hasMatchingTask = false;
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                System.out.println("    " + task.getStatus());
                hasMatchingTask = true;
            }
        }

        if (!hasMatchingTask) {
            System.out.println("    There are no tasks on this date.");
        }
        System.out.println(horizLine);
    }

    private static String getGreeting() {
        // This ASCII art for VERITY was generated by Codex Luna High.
        String banner = "V   V  EEEEE  RRRR   IIIII  TTTTT  Y   Y\n"
                + "V   V  E      R   R    I      T     Y Y\n"
                + " V V   EEEE   RRRR     I      T      Y\n"
                + "  V    E      R R      I      T      Y\n"
                + "  V    EEEEE  R  RR  IIIII    T      Y";
        return horizLine
                + banner + "\n\n"
                + "Hello! I'm Verity.\n"
                + "I speak only the truth.\n"
                + "What can I do for you?\n"
                + horizLine;
    }

    private static String getExitString() {
        return "____________________________________________________________\n"
                + "    Bye. Hope to see you again soon!\n"
                + "____________________________________________________________\n";
    }
}
