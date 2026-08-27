package verity.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.command.AddCommand;
import verity.command.Command;
import verity.command.DeleteCommand;
import verity.command.ExitCommand;
import verity.command.FindCommand;
import verity.command.ListCommand;
import verity.command.MarkCommand;
import verity.command.UnmarkCommand;
import verity.exception.VerityException;
import verity.storage.Storage;
import verity.task.Deadline;
import verity.task.Event;
import verity.task.Task;
import verity.task.TaskList;
import verity.task.Todo;
import verity.ui.Ui;

/**
 * Tests user-command parsing and saved-task reconstruction.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @TempDir
    Path temporaryDirectory;

    @Test
    void parse_exitCommand_returnsExitCommand() throws VerityException {
        Command command = parser.parse("bye", 0);

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    void parse_nonMutatingCommands_returnsCorrectCommandTypes()
            throws VerityException {
        assertInstanceOf(
                ListCommand.class,
                parser.parse("list", 0));
        assertInstanceOf(
                FindCommand.class,
                parser.parse("find 2026-08-10", 0));
        assertInstanceOf(
                MarkCommand.class,
                parser.parse("mark 1", 1));
        assertInstanceOf(
                UnmarkCommand.class,
                parser.parse("unmark 1", 1));
        assertInstanceOf(
                DeleteCommand.class,
                parser.parse("delete 1", 1));
    }

    @Test
    void parse_todoCommand_createsTodoAddCommand() throws Exception {
        Command command = parser.parse("todo read book", 0);
        TaskList tasks = new TaskList();

        command.execute(
                tasks,
                new Ui(),
                new Storage(temporaryDirectory.resolve("todo.txt")));

        assertInstanceOf(AddCommand.class, command);
        assertEquals("[T][ ] read book", tasks.get(0).getStatus());
    }

    @Test
    void parse_deadlineCommand_createsDeadlineAddCommand() throws Exception {
        Command command = parser.parse(
                "deadline submit report /by 2026-08-10", 0);
        TaskList tasks = new TaskList();

        command.execute(
                tasks,
                new Ui(),
                new Storage(temporaryDirectory.resolve("deadline.txt")));

        assertInstanceOf(AddCommand.class, command);
        assertEquals(
                "[D][ ] submit report (by: Aug 10 2026)",
                tasks.get(0).getStatus());
    }

    @Test
    void parse_eventCommand_createsEventAddCommand() throws Exception {
        Command command = parser.parse(
                "event project meeting /from 2026-08-10 /to 2026-08-12",
                0);
        TaskList tasks = new TaskList();

        command.execute(
                tasks,
                new Ui(),
                new Storage(temporaryDirectory.resolve("event.txt")));

        assertInstanceOf(AddCommand.class, command);
        assertEquals(
                "[E][ ] project meeting (from: Aug 10 2026 to: Aug 12 2026)",
                tasks.get(0).getStatus());
    }

    @Test
    void parse_unknownCommand_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("unknown", 0));

        assertEquals("I don't know that command.", exception.getMessage());
    }

    @Test
    void parse_invalidTaskNumber_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("delete 0", 1));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    void parse_missingTaskNumber_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("mark", 1));

        assertEquals("Please provide a task number.", exception.getMessage());
    }

    @Test
    void parse_missingTodoDescription_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("todo", 0));

        assertEquals(
                "The description of a todo cannot be empty.",
                exception.getMessage());
    }

    @Test
    void parse_invalidDeadlineDate_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "deadline submit report /by 10-08-2026", 0));

        assertEquals(
                "Dates must use the format yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    void parse_reversedEventDates_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse(
                        "event meeting /from 2026-08-12 /to 2026-08-10",
                        0));

        assertEquals(
                "The event end date cannot be before the start date.",
                exception.getMessage());
    }

    @Test
    void parse_invalidFindCommand_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parse("find", 0));

        assertEquals(
                "Use find followed by a date in yyyy-MM-dd format.",
                exception.getMessage());
    }

    @Test
    void parseSavedTasks_validLines_reconstructAllTaskTypes()
            throws VerityException {
        List<Task> tasks = parser.parseSavedTasks(List.of(
                "T\t0\tread book",
                "D\t1\tsubmit report\t2026-08-10",
                "E\t0\tproject meeting\t2026-08-10\t2026-08-12"
        ));

        assertInstanceOf(Todo.class, tasks.get(0));
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("[T][ ] read book", tasks.get(0).getStatus());
        assertEquals(
                "[D][X] submit report (by: Aug 10 2026)",
                tasks.get(1).getStatus());
        assertEquals(
                "[E][ ] project meeting (from: Aug 10 2026 to: Aug 12 2026)",
                tasks.get(2).getStatus());
    }

    @Test
    void parseSavedTasks_emptyLines_returnsEmptyList()
            throws VerityException {
        assertEquals(List.of(), parser.parseSavedTasks(List.of()));
    }

    @Test
    void parseSavedTasks_corruptedLineIncludesLineNumber() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parseSavedTasks(List.of(
                        "T\t0\tread book",
                        "X\t0\tunknown task"
                )));

        assertEquals(
                "Line 2: unknown task type 'X'.",
                exception.getMessage());
    }

    @Test
    void parseSavedTasks_invalidStatus_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parseSavedTasks(List.of("T\t2\tread book")));

        assertEquals(
                "Line 1: completion status must be 0 or 1.",
                exception.getMessage());
    }

    @Test
    void parseSavedTasks_blankField_throwsVerityException() {
        VerityException exception = assertThrows(
                VerityException.class,
                () -> parser.parseSavedTasks(List.of("T\t0\t")));

        assertEquals(
                "Line 1: task fields cannot be empty.",
                exception.getMessage());
    }
}
