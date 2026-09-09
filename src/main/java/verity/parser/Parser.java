package verity.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import verity.command.AddCommand;
import verity.command.Command;
import verity.command.DeleteCommand;
import verity.command.ExitCommand;
import verity.command.FindCommand;
import verity.command.ListCommand;
import verity.command.MarkCommand;
import verity.command.UnmarkCommand;
import verity.exception.VerityException;
import verity.task.Deadline;
import verity.task.Event;
import verity.task.Task;
import verity.task.Todo;

/**
 * Interprets user commands and reconstructs tasks from saved data.
 */
public class Parser {

    private static final String DEADLINE_DATE_MARKER = "/by";
    private static final String EVENT_START_DATE_MARKER = "/from";
    private static final String EVENT_END_DATE_MARKER = "/to";

    private static final String TASK_FIELD_SEPARATOR = "\t";
    private static final String TASK_TYPE_TODO = "T";
    private static final String TASK_TYPE_DEADLINE = "D";
    private static final String TASK_TYPE_EVENT = "E";
    private static final String TASK_STATUS_INCOMPLETE = "0";
    private static final String TASK_STATUS_COMPLETE = "1";

    private static final int TASK_TYPE_INDEX = 0;
    private static final int TASK_STATUS_INDEX = 1;
    private static final int TASK_DESCRIPTION_INDEX = 2;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final int MINIMUM_TASK_FIELD_COUNT = 3;
    private static final int DEADLINE_DATE_INDEX = 3;
    private static final int EVENT_START_DATE_INDEX = 3;
    private static final int EVENT_END_DATE_INDEX = 4;

    /**
     * Parses user input and creates the command to execute.
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

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(commandParts[1]) - 1;
        } catch (NumberFormatException exception) {
            throw new VerityException("The task number must be a number.");
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new VerityException("That task number does not exist.");
        }
        return taskIndex;
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
        int byIndex = findMarker(commandParts, 1, DEADLINE_DATE_MARKER);
        validateDeadlineArguments(commandParts, byIndex);

        String description = joinWords(commandParts, 1, byIndex);
        String dateText = joinWords(commandParts, byIndex + 1, commandParts.length);

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
        int fromIndex = findMarker(
                commandParts, 1, EVENT_START_DATE_MARKER);
        validateEventStartArguments(commandParts, fromIndex);

        int toIndex = findMarker(
                commandParts, fromIndex + 1, EVENT_END_DATE_MARKER);
        validateEventEndArguments(commandParts, fromIndex, toIndex);

        String description = joinWords(commandParts, 1, fromIndex);
        LocalDate fromDate = parseDate(joinWords(
                commandParts, fromIndex + 1, toIndex));
        LocalDate toDate = parseDate(joinWords(
                commandParts, toIndex + 1, commandParts.length));

        return createEvent(description, fromDate, toDate);
    }
    /**
     * Validates the description and date arguments of a deadline command.
     *
     * @param commandParts Parts of the deadline command.
     * @param byIndex Index of the {@code /by} marker.
     * @throws VerityException If a required argument is missing.
     */
    private void validateDeadlineArguments(String[] commandParts, int byIndex) throws VerityException {
        if (byIndex == 1) {
            throw new VerityException("The description of a deadline cannot be empty.");
        }

        if (byIndex == commandParts.length) {
            throw new VerityException("A deadline must include a /by date.");
        }

        if (byIndex == commandParts.length - 1) {
            throw new VerityException("The deadline date cannot be empty.");
        }
    }

    /**
     * Validates an event description and its {@code /from} marker.
     *
     * @param commandParts Parts of the event command.
     * @param fromIndex Index of the {@code /from} marker.
     * @throws VerityException If the description or marker is missing.
     */
    private void validateEventStartArguments(
            String[] commandParts, int fromIndex)
            throws VerityException {
        if (fromIndex == 1) {
            throw new VerityException(
                    "The description of an event cannot be empty.");
        }

        if (fromIndex == commandParts.length) {
            throw new VerityException(
                    "An event must include a /from date and a /to date.");
        }
    }

    /**
     * Validates the date arguments following an event's {@code /from} marker.
     *
     * @param commandParts Parts of the event command.
     * @param fromIndex Index of the {@code /from} marker.
     * @param toIndex Index of the {@code /to} marker.
     * @throws VerityException If either event date is missing.
     */
    private void validateEventEndArguments(
            String[] commandParts, int fromIndex, int toIndex)
            throws VerityException {
        if (fromIndex + 1 == toIndex) {
            throw new VerityException(
                    "The event's from date cannot be empty.");
        }

        if (toIndex == commandParts.length) {
            throw new VerityException(
                    "An event must include a /to date.");
        }

        if (toIndex + 1 == commandParts.length) {
            throw new VerityException(
                    "The event's to date cannot be empty.");
        }
    }

    private int findMarker(String[] commandParts, int startIndex, String marker) {
        int markerIndex = startIndex;

        while (markerIndex < commandParts.length && !commandParts[markerIndex].equals(marker)) {
            markerIndex++;
        }

        return markerIndex;
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

    /**
     * Parses a date in ISO-8601 format.
     *
     * @param dateText Date text to parse.
     * @return Parsed date.
     * @throws VerityException If the date text is invalid.
     */
    private LocalDate parseDate(String dateText)
            throws VerityException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new VerityException(
                    "Dates must use the format yyyy-MM-dd.");
        }
    }

    /**
     * Creates an event after validating its date range.
     *
     * @param description Description of the event.
     * @param fromDate First date of the event.
     * @param toDate Last date of the event.
     * @return Event with the specified details.
     * @throws VerityException If the end date is before the start date.
     */
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
        String[] fields = splitTaskLine(taskLine);
        String storedStatus = fields[TASK_STATUS_INDEX];

        validateStoredStatus(storedStatus);

        Task task = createTaskFromFields(fields);

        validateTaskFields(fields);
        restoreCompletionStatus(task, storedStatus);

        return task;
    }

    /**
     * Splits a saved task line into its individual fields.
     *
     * @param taskLine Saved task line.
     * @return Fields contained in the saved line.
     * @throws VerityException If the line has too few fields.
     */
    private String[] splitTaskLine(String taskLine)
            throws VerityException {
        String[] fields = taskLine.split(TASK_FIELD_SEPARATOR, -1);

        if (fields.length < MINIMUM_TASK_FIELD_COUNT) {
            throw new VerityException(
                    "expected at least three fields.");
        }

        return fields;
    }

    /**
     * Validates a stored completion status.
     *
     * @param storedStatus Completion status from the data file.
     * @throws VerityException If the status is not recognized.
     */
    private void validateStoredStatus(String storedStatus)
            throws VerityException {
        boolean isIncomplete =
                storedStatus.equals(TASK_STATUS_INCOMPLETE);
        boolean isComplete =
                storedStatus.equals(TASK_STATUS_COMPLETE);

        if (!isIncomplete && !isComplete) {
            throw new VerityException(
                    "completion status must be 0 or 1.");
        }
    }

    /**
     * Creates a task from validated saved fields.
     *
     * @param fields Fields from a saved task line.
     * @return Reconstructed task.
     * @throws VerityException If the task type or its fields are invalid.
     */
    private Task createTaskFromFields(String[] fields)
            throws VerityException {
        String taskType = fields[TASK_TYPE_INDEX];

        return switch (taskType) {
            case TASK_TYPE_TODO -> createTodoFromFields(fields);
            case TASK_TYPE_DEADLINE -> createDeadlineFromFields(fields);
            case TASK_TYPE_EVENT -> createEventFromFields(fields);
            default -> throw new VerityException(
                    "unknown task type '" + taskType + "'.");
        };
    }

    private Todo createTodoFromFields(String[] fields)
            throws VerityException {
        validateFieldCount(
                fields,
                TODO_FIELD_COUNT,
                "a todo must have exactly three fields.");

        return new Todo(fields[TASK_DESCRIPTION_INDEX]);
    }

    private Deadline createDeadlineFromFields(String[] fields)
            throws VerityException {
        validateFieldCount(
                fields,
                DEADLINE_FIELD_COUNT,
                "a deadline must have exactly four fields.");

        return new Deadline(
                fields[TASK_DESCRIPTION_INDEX],
                parseDate(fields[DEADLINE_DATE_INDEX]));
    }

    private Event createEventFromFields(String[] fields)
            throws VerityException {
        validateFieldCount(
                fields,
                EVENT_FIELD_COUNT,
                "an event must have exactly five fields.");

        return createEvent(
                fields[TASK_DESCRIPTION_INDEX],
                parseDate(fields[EVENT_START_DATE_INDEX]),
                parseDate(fields[EVENT_END_DATE_INDEX]));
    }

    /**
     * Validates the number of fields used to represent a task.
     *
     * @param fields Saved task fields.
     * @param expectedFieldCount Required number of fields.
     * @param errorMessage Message to use when validation fails.
     * @throws VerityException If the field count is incorrect.
     */
    private void validateFieldCount(
            String[] fields,
            int expectedFieldCount,
            String errorMessage) throws VerityException {
        if (fields.length != expectedFieldCount) {
            throw new VerityException(errorMessage);
        }
    }

    /**
     * Checks that the task's description and date fields are not blank.
     *
     * @param fields Saved task fields.
     * @throws VerityException If a required field is blank.
     */
    private void validateTaskFields(String[] fields)
            throws VerityException {
        for (int i = TASK_DESCRIPTION_INDEX; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new VerityException(
                        "task fields cannot be empty.");
            }
        }
    }

    /**
     * Restores a task's saved completion status.
     *
     * @param task Task whose status should be restored.
     * @param storedStatus Completion status from the data file.
     */
    private void restoreCompletionStatus(
            Task task, String storedStatus) {
        if (storedStatus.equals(TASK_STATUS_COMPLETE)) {
            task.markAsDone();
        }
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
