import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class Verity {
	public static final String horizLine = "____________________________________________________________\n";

	public enum Command {
		TODO,
		DEADLINE,
		EVENT,
		LIST,
		MARK,
		UNMARK,
		DELETE,
		BYE
	}

	private static final Path DATA_FILE_PATH = Path.of("data", "verity.txt");

	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);

		String greeting = getGreeting();
		System.out.println(greeting);

		ArrayList<Task> tasks = new ArrayList<>();
		List<String> savedTaskLines = readTaskLinesFromFile();
		while (true) {
			String command = scanner.nextLine();
			String[] commandParts = command.trim().split("\\s+");
			try {
				Command commandType = parseCommand(commandParts[0]);
				int n = commandParts.length;
				if (commandType == Command.BYE) {
					break;
				}
				else if (commandType == Command.MARK) {
					int taskNumber = getTaskNumber(commandParts, tasks.size());
					tasks.get(taskNumber).markAsDone();
					System.out.println(horizLine);
					System.out.println("Nice! I've marked this task as done:\n");
					System.out.println(tasks.get(taskNumber).getStatus() + "\n" + horizLine);
				} else if (commandType == Command.UNMARK) {
					int taskNumber = getTaskNumber(commandParts, tasks.size());
					tasks.get(taskNumber).markAsUndone();
					System.out.println(horizLine);
					System.out.println("Ok, I've marked this task as not done yet:\n");
					System.out.println(tasks.get(taskNumber).getStatus() + "\n" + horizLine);

				} else if (commandType == Command.LIST) {
					System.out.println(horizLine);
					System.out.println("    Here are the tasks in your list:\n");
					for (int i = 0; i < tasks.size(); ++i) {
						System.out.println("    " + (i + 1) + "." + tasks.get(i).getStatus());
					}
					System.out.println(horizLine);
				} else if (commandType == Command.DELETE) {
					int taskNumber = getTaskNumber(commandParts, tasks.size());
					Task removedTask = tasks.remove(taskNumber);
					System.out.println(horizLine);
					System.out.println("Noted. I've removed this task:\n");
					System.out.println(removedTask.getStatus() + "\n" + horizLine);
					System.out.println("Now you have " + tasks.size() + " tasks in the list.\n");
					System.out.println(horizLine);
				} else if (commandType == Command.TODO) {
					if (n == 1) {
						throw new VerityException("The description of a todo cannot be empty.");
					}
					// String concatenation optimization assisted by ChatGPT SOL
					StringBuilder description =  new StringBuilder();
					for (int i = 1; i < n; i++) {
						if (i > 1) {
							description.append(" ");
						}
						description.append(commandParts[i]);
					}
					String desc = description.toString();
					Todo toDoEvent = new Todo(desc);
					tasks.add(toDoEvent);
					printAddedMessage(toDoEvent, tasks.size());
				} else if (commandType == Command.DEADLINE) {
					if (n == 1) {
						throw new VerityException("The description of a deadline cannot be empty.");
					}
					StringBuilder description = new StringBuilder();
					int i = 1;
					while (i < n && !commandParts[i].equals("/by")) {
						if (i > 1) {
							description.append(" ");
						}
						description.append(commandParts[i]);
						i++;
					}
					if (i == 1) {
						throw new VerityException("The description of a deadline cannot be empty.");
					}
					if (i == n) {
						throw new VerityException("A deadline must include a /by date.");
					}
					String desc = description.toString();
					i++;
					if (i == n) {
						throw new VerityException("The deadline date cannot be empty.");
					}
					StringBuilder by = new StringBuilder();
					for (int j = i; j < n; j++) {
						if (j > i) {
							by.append(" ");
						}
						by.append(commandParts[j]);
					}
					String byDate = by.toString();
					Deadline deadlineTask = new Deadline(desc, byDate);
					tasks.add(deadlineTask);
					printAddedMessage(deadlineTask, tasks.size());
				} else if (commandType == Command.EVENT) {
					if (n == 1) {
						throw new VerityException("The description of an event cannot be empty.");
					}
					StringBuilder description = new StringBuilder();
					int i = 1;
					while (i < n && !commandParts[i].equals("/from")) {
						if (i > 1) {
							description.append(" ");
						}
						description.append(commandParts[i]);
						i++;
					}
					if (i == 1) {
						throw new VerityException("The description of an event cannot be empty.");
					}
					if (i == n) {
						throw new VerityException("An event must include a /from time and a /to time.");
					}
					String desc = description.toString();
					i++;
					if (i == n || commandParts[i].equals("/to")) {
						throw new VerityException("The event's from time cannot be empty.");
					}
					StringBuilder from = new StringBuilder();
					int j = i;
					while (j < n && !commandParts[j].equals("/to")) {
						if (j > i) {
							from.append(" ");
						}
						from.append(commandParts[j]);
						j++;
					}
					if (j == n) {
						throw new VerityException("An event must include a /to time.");
					}
					String fromDate = from.toString();
					j++;
					if (j == n) {
						throw new VerityException("The event's to time cannot be empty.");
					}
					StringBuilder to = new StringBuilder();
					for (int k = j; k < n; k++) {
						if (k > j) {
							to.append(" ");
						}
						to.append(commandParts[k]);
					}
					String toDate = to.toString();
					Event eventTask = new Event(desc, fromDate, toDate);
					tasks.add(eventTask);
					printAddedMessage(eventTask, tasks.size());
				} else {
					throw new VerityException("Start with todo, deadline or event.");
				}
			} catch (VerityException e) {
				System.out.println(horizLine
						+ "     Speak your truth. " + e.getMessage() + "\n"
						+ horizLine);
			}
		}
		String exitStr = getExitString();
		System.out.println(exitStr);
	}

	/**
	 * Writes the supplied contents to the data file.
	 *
	 * @param fileContents Contents to write to the data file.
	 * @throws IOException If the data file cannot be written.
	 */
	private static void writeToFile(String fileContents) throws IOException {
		Files.writeString(DATA_FILE_PATH, fileContents, StandardCharsets.UTF_8);
	}

	private static List<String> readTaskLinesFromFile() throws IOException {
		return Files.readAllLines(DATA_FILE_PATH, StandardCharsets.UTF_8);
	}

	private static int getTaskNumber(String[] commandParts, int taskSize) throws VerityException {
		if (commandParts.length < 2) {
			throw new VerityException("Please provide a task number.");
		}

		int taskNumber;
		try {
			taskNumber = Integer.parseInt(commandParts[1]) - 1;
		} catch (NumberFormatException e) {
			throw new VerityException("The task number must be a number.");
		}

		if (taskNumber < 0 || taskNumber >= taskSize) {
			throw new VerityException("That task number does not exist.");
		}
		return taskNumber;
	}

	private static void printAddedMessage(Task task, int taskCount) {
		System.out.println(horizLine
				+ "     Got it. I've added this task:\n"
				+ "       " + task.getStatus() + "\n"
				+ "     Now you have " + taskCount + " tasks in the list.\n"
				+ horizLine);
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

	private static Command parseCommand(String word) throws VerityException {
		try {
			return Command.valueOf(word.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new VerityException("I don't know that command.");
		}
	}
}
