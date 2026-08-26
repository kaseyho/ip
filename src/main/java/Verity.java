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

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		String greeting = getGreeting();
		System.out.println(greeting);

		ArrayList<Task> tasks;
		try {
			List<String> savedTaskLines = readTaskLinesFromFile();
			tasks = parseSavedTasks(savedTaskLines);
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
				Command commandType = parseCommand(commandParts[0]);
				int n = commandParts.length;
				if (commandType == Command.BYE) {
					break;
				}
				else if (commandType == Command.MARK) {
					int taskNumber = getTaskNumber(commandParts, tasks.size());
					tasks.get(taskNumber).markAsDone();
					saveTasks(tasks);
					System.out.println(horizLine);
					System.out.println("Nice! I've marked this task as done:\n");
					System.out.println(tasks.get(taskNumber).getStatus() + "\n" + horizLine);
				} else if (commandType == Command.UNMARK) {
					int taskNumber = getTaskNumber(commandParts, tasks.size());
					tasks.get(taskNumber).markAsUndone();
					saveTasks(tasks);
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
					saveTasks(tasks);
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
					saveTasks(tasks);
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
					saveTasks(tasks);
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
					saveTasks(tasks);
					printAddedMessage(eventTask, tasks.size());
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

	/**
	 * Writes the supplied contents to the data file.
	 *
	 * @param fileContents Contents to write to the data file.
	 * @throws IOException If the data directory or file cannot be written.
	 */
	private static void writeToFile(String fileContents) throws IOException {
		Files.createDirectories(DATA_FILE_PATH.getParent());
		Files.writeString(DATA_FILE_PATH, fileContents, StandardCharsets.UTF_8);
	}

	/**
	 * Returns the task lines stored in the data file, or an empty list if the file does not exist.
	 *
	 * @return Task lines stored in the data file.
	 * @throws IOException If the data file exists but cannot be read.
	 */
	private static List<String> readTaskLinesFromFile() throws IOException {
		if (Files.notExists(DATA_FILE_PATH)) {
			return new ArrayList<>();
		}
		return Files.readAllLines(DATA_FILE_PATH, StandardCharsets.UTF_8);
	}

	/**
	 * Writes all tasks to the data file.
	 *
	 * @param tasks Tasks to write to the data file.
	 * @throws IOException If the tasks cannot be written.
	 */
	private static void saveTasks(List<Task> tasks) throws IOException {
		StringBuilder fileContents = new StringBuilder();
		for (Task task : tasks) {
			fileContents.append(task.serialize()).append(System.lineSeparator());
		}
		writeToFile(fileContents.toString());
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

	/**
	 * Returns tasks reconstructed from saved task lines.
	 *
	 * @param savedTaskLines Lines read from the data file.
	 * @return Reconstructed tasks.
	 * @throws VerityException If a saved line is corrupted.
	 */
	private static ArrayList<Task> parseSavedTasks(List<String> savedTaskLines) throws VerityException {
		ArrayList<Task> tasks = new ArrayList<>();
		for (int i = 0; i < savedTaskLines.size(); i++) {
			try {
				tasks.add(parseTaskLine(savedTaskLines.get(i)));
			} catch (VerityException exception) {
				throw new VerityException(
						"Line " + (i + 1) + ": " + exception.getMessage());
			}
		}
		return tasks;
	}

	/**
	 * Returns the task represented by one saved data line.
	 *
	 * @param taskLine Saved data line to parse.
	 * @return Reconstructed task.
	 * @throws VerityException If the line has an invalid format.
	 */
	private static Task parseTaskLine(String taskLine)
			throws VerityException {
		String[] fields = taskLine.split("\t", -1);
		if (fields.length < 3) {
			throw new VerityException("expected at least three fields.");
		}

		String taskType = fields[0];
		String storedStatus = fields[1];
		if (!storedStatus.equals("0") && !storedStatus.equals("1")) {
			throw new VerityException(
					"completion status must be 0 or 1.");
		}

		Task task;
		switch (taskType) {
			case "T" -> {
				if (fields.length != 3) {
					throw new VerityException(
							"a todo must have exactly three fields.");
				}
				task = new Todo(fields[2]);
			}
			case "D" -> {
				if (fields.length != 4) {
					throw new VerityException(
							"a deadline must have exactly four fields.");
				}
				task = new Deadline(fields[2], fields[3]);
			}
			case "E" -> {
				if (fields.length != 5) {
					throw new VerityException(
							"an event must have exactly five fields.");
				}
				task = new Event(fields[2], fields[3], fields[4]);
			}
			default -> throw new VerityException(
					"unknown task type '" + taskType + "'.");
		}

		for (int i = 2; i < fields.length; i++) {
			if (fields[i].isBlank()) {
				throw new VerityException(
						"task fields cannot be empty.");
			}
		}

		if (storedStatus.equals("1")) {
			task.markAsDone();
		}
		return task;
	}


}
