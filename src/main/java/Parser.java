import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Interprets user commands and reconstructs tasks from saved data.
 */
public class Parser {

    /**
     * Parses a user command and creates the command to execute.
     *
     * @param fullCommand Complete command entered by the user.
     * @param taskCount Number of tasks currently stored.
     * @return Command corresponding to the user input.
     * @throws VerityException If the command or its arguments are invalid.
     */
    public Command parse(String fullCommand, int taskCount)
            throws VerityException {
        String[] commandParts = fullCommand.trim().split("\\s+");
        String commandWord =
                commandParts[0].toLowerCase(Locale.ROOT);

        return switch (commandWord) {
            case "bye" -> new ExitCommand();
            case "list" -> new ListCommand();
            case "mark" -> new MarkCommand(
                    parseTaskNumber(commandParts, taskCount));
            case "unmark" -> new UnmarkCommand(
                    parseTaskNumber(commandParts, taskCount));
            case "delete" -> new DeleteCommand(
                    parseTaskNumber(commandParts, taskCount));
            case "todo" -> new AddCommand(
                    parseTodo(commandParts));
            case "deadline" -> new AddCommand(
                    parseDeadline(commandParts));
            case "event" -> new AddCommand(
                    parseEvent(commandParts));
            case "find" -> new FindCommand(
                    parseFindDate(commandParts));
            default -> throw new VerityException(
                    "I don't know that command.");
        };
    }

    /**
     * Parses and validates a one-based task number.
     *
     * @param commandParts Parts of the user command.
     * @param taskCount Number of tasks currently stored.
     * @return Corresponding zero-based task index.
     * @throws VerityException If the task number is missing or invalid.
     */
    private int parseTaskNumber(String[] commandParts, int taskCount)
            throws VerityException {
        if (commandParts.length < 2) {
            throw new VerityException("Please provide a task number.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(commandParts[1]) - 1;
        } catch (NumberFormatException exception) {
            throw new VerityException("The task number must be a number.");
        }

        if (taskNumber < 0 || taskNumber >= taskCount) {
            throw new VerityException("That task number does not exist.");
        }
        return taskNumber;
    }

    /**
     * Parses a todo command.
     *
     * @param commandParts Parts of the user command.
     * @return Parsed todo.
     * @throws VerityException If the description is missing.
     */
    private Todo parseTodo(String[] commandParts) throws VerityException {
        if (commandParts.length == 1) {
            throw new VerityException(
                    "The description of a todo cannot be empty.");
        }

        String description = joinWords(
                commandParts, 1, commandParts.length);
        return new Todo(description);
    }

    /**
     * Parses a deadline command.
     *
     * @param commandParts Parts of the user command.
     * @return Parsed deadline.
     * @throws VerityException If its description or date is invalid.
     */
    private Deadline parseDeadline(String[] commandParts)
            throws VerityException {
        int partCount = commandParts.length;
        int byIndex = 1;

        while (byIndex < partCount
                && !commandParts[byIndex].equals("/by")) {
            byIndex++;
        }

        if (byIndex == 1) {
            throw new VerityException(
                    "The description of a deadline cannot be empty.");
        }
        if (byIndex == partCount) {
            throw new VerityException(
                    "A deadline must include a /by date.");
        }
        if (byIndex + 1 == partCount) {
            throw new VerityException(
                    "The deadline date cannot be empty.");
        }

        String description = joinWords(commandParts, 1, byIndex);
        String dateText = joinWords(
                commandParts, byIndex + 1, partCount);

        return new Deadline(description, parseDate(dateText));
    }

    /**
     * Parses an event command.
     *
     * @param commandParts Parts of the user command.
     * @return Parsed event.
     * @throws VerityException If its description or dates are invalid.
     */
    private Event parseEvent(String[] commandParts)
            throws VerityException {
        int partCount = commandParts.length;
        int fromIndex = 1;

        while (fromIndex < partCount
                && !commandParts[fromIndex].equals("/from")) {
            fromIndex++;
        }

        if (fromIndex == 1) {
            throw new VerityException(
                    "The description of an event cannot be empty.");
        }
        if (fromIndex == partCount) {
            throw new VerityException(
                    "An event must include a /from date and a /to date.");
        }

        int toIndex = fromIndex + 1;
        while (toIndex < partCount
                && !commandParts[toIndex].equals("/to")) {
            toIndex++;
        }

        if (fromIndex + 1 == toIndex) {
            throw new VerityException(
                    "The event's from date cannot be empty.");
        }
        if (toIndex == partCount) {
            throw new VerityException(
                    "An event must include a /to date.");
        }
        if (toIndex + 1 == partCount) {
            throw new VerityException(
                    "The event's to date cannot be empty.");
        }

        String description = joinWords(commandParts, 1, fromIndex);
        String fromDateText = joinWords(
                commandParts, fromIndex + 1, toIndex);
        String toDateText = joinWords(
                commandParts, toIndex + 1, partCount);

        return createEvent(
                description,
                parseDate(fromDateText),
                parseDate(toDateText));
    }

    /**
     * Parses the date supplied to a find command.
     *
     * @param commandParts Parts of the user command.
     * @return Date to search for.
     * @throws VerityException If the command does not contain one date.
     */
    private LocalDate parseFindDate(String[] commandParts)
            throws VerityException {
        if (commandParts.length != 2) {
            throw new VerityException(
                    "Use find followed by a date in yyyy-MM-dd format.");
        }
        return parseDate(commandParts[1]);
    }

    /**
     * Reconstructs tasks from saved task lines.
     *
     * @param savedTaskLines Lines read from the data file.
     * @return Reconstructed tasks.
     * @throws VerityException If a saved line is corrupted.
     */
    public List<Task> parseSavedTasks(
            List<String> savedTaskLines) throws VerityException {
        List<Task> tasks = new ArrayList<>();

        for (int i = 0; i < savedTaskLines.size(); i++) {
            try {
                tasks.add(parseTaskLine(savedTaskLines.get(i)));
            } catch (VerityException exception) {
                throw new VerityException(
                        "Line " + (i + 1) + ": "
                                + exception.getMessage());
            }
        }
        return tasks;
    }

    private LocalDate parseDate(String dateText)
            throws VerityException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new VerityException(
                    "Dates must use the format yyyy-MM-dd.");
        }
    }

    private Event createEvent(String description, LocalDate fromDate,
                              LocalDate toDate) throws VerityException {
        if (toDate.isBefore(fromDate)) {
            throw new VerityException(
                    "The event end date cannot be before the start date.");
        }
        return new Event(description, fromDate, toDate);
    }

    /**
     * Reconstructs one task from a saved data line.
     *
     * @param taskLine Saved task line.
     * @return Reconstructed task.
     * @throws VerityException If the line is corrupted.
     */
    private Task parseTaskLine(String taskLine)
            throws VerityException {
        String[] fields = taskLine.split("\t", -1);
        if (fields.length < 3) {
            throw new VerityException(
                    "expected at least three fields.");
        }

        String taskType = fields[0];
        String storedStatus = fields[1];
        if (!storedStatus.equals("0")
                && !storedStatus.equals("1")) {
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
                task = new Deadline(fields[2], parseDate(fields[3]));
            }
            case "E" -> {
                if (fields.length != 5) {
                    throw new VerityException(
                            "an event must have exactly five fields.");
                }
                task = createEvent(
                        fields[2],
                        parseDate(fields[3]),
                        parseDate(fields[4]));
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

    private String joinWords(String[] commandParts, int startIndex,
                             int endIndex) {
        StringBuilder result = new StringBuilder();

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                result.append(" ");
            }
            result.append(commandParts[i]);
        }
        return result.toString();
    }
}