package verity.parser;

import java.util.ArrayList;
import java.util.List;

import verity.exception.VerityException;
import verity.task.Deadline;
import verity.task.Event;
import verity.task.Task;
import verity.task.Todo;

/**
 * Reconstructs tasks from their saved data representation.
 */
class SavedTaskParser {
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
     * Reconstructs tasks from saved task lines.
     *
     * @param savedTaskLines Lines read from the data file.
     * @return Reconstructed tasks.
     * @throws VerityException If a saved line is corrupted.
     */
    List<Task> parseSavedTasks(
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
                TaskFactory.parseDate(fields[DEADLINE_DATE_INDEX]));
    }

    private Event createEventFromFields(String[] fields)
            throws VerityException {
        validateFieldCount(
                fields,
                EVENT_FIELD_COUNT,
                "an event must have exactly five fields.");

        return TaskFactory.createEvent(
                fields[TASK_DESCRIPTION_INDEX],
                TaskFactory.parseDate(fields[EVENT_START_DATE_INDEX]),
                TaskFactory.parseDate(fields[EVENT_END_DATE_INDEX]));
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
}
