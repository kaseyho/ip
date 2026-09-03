package verity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.storage.Storage;
import verity.task.Deadline;
import verity.task.TaskList;
import verity.task.Todo;
import verity.ui.Ui;

/**
 * Tests command execution and command exit state.
 */
class CommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void exitCommand_isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    void nonExitCommands_isExit_returnsFalse() {
        assertFalse(new AddCommand(new Todo("read book")).isExit());
        assertFalse(new DeleteCommand(0).isExit());
        assertFalse(new FindCommand("book").isExit());
        assertFalse(new FindDateCommand(
                LocalDate.of(2026, 8, 10)).isExit());
        assertFalse(new ListCommand().isExit());
        assertFalse(new MarkCommand(0).isExit());
        assertFalse(new UnmarkCommand(0).isExit());
    }

    @Test
    void addCommand_execute_addsTaskAndSavesIt() throws IOException {
        TaskList tasks = new TaskList();
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Todo todo = new Todo("read book");

        new AddCommand(todo).execute(
                tasks, new Ui(), new Storage(dataFile));

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
        assertEquals(
                "T\t0\tread book" + System.lineSeparator(),
                Files.readString(dataFile));
    }

    @Test
    void deleteCommand_execute_removesTaskAndSavesIt() throws IOException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(todo);
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        new DeleteCommand(0).execute(
                tasks, new Ui(), new Storage(dataFile));

        assertEquals(0, tasks.size());
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    void markCommand_execute_marksTaskAndSavesIt() throws IOException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(todo);
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        new MarkCommand(0).execute(
                tasks, new Ui(), new Storage(dataFile));

        assertEquals("[T][X] read book", tasks.get(0).getStatus());
        assertEquals(
                "T\t1\tread book" + System.lineSeparator(),
                Files.readString(dataFile));
    }

    @Test
    void unmarkCommand_execute_unmarksTaskAndSavesIt() throws IOException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(todo);
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        new UnmarkCommand(0).execute(
                tasks, new Ui(), new Storage(dataFile));

        assertEquals("[T][ ] read book", tasks.get(0).getStatus());
        assertEquals(
                "T\t0\tread book" + System.lineSeparator(),
                Files.readString(dataFile));
    }

    @Test
    void listCommand_execute_displaysAllTasks() {
        TaskList tasks = new TaskList(new Todo("read book"));

        String output = captureOutput(
                () -> new ListCommand().execute(
                        tasks, new Ui(), new Storage(temporaryDirectory)));

        assertTrue(output.contains("1.[T][ ] read book"));
    }

    @Test
    void findCommand_execute_displaysOnlyKeywordMatches() {
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Todo("submit report"),
                new Todo("return BOOK")
        );

        String output = captureOutput(
                () -> new FindCommand("book").execute(
                        tasks, new Ui(), new Storage(temporaryDirectory)));

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] return BOOK"));
        assertFalse(output.contains("[T][ ] submit report"));
    }

    @Test
    void findDateCommand_execute_displaysTasksOnDate() {
        LocalDate date = LocalDate.of(2026, 8, 10);
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", date)
        );

        String output = captureOutput(
                () -> new FindDateCommand(date).execute(
                        tasks, new Ui(), new Storage(temporaryDirectory)));

        assertTrue(output.contains("Tasks on 2026-08-10:"));
        assertTrue(output.contains("[D][ ] submit report"));
        assertFalse(output.contains("[T][ ] read book"));
    }

    @Test
    void exitCommand_execute_doesNotChangeTasks() {
        TaskList tasks = new TaskList();

        new ExitCommand().execute(
                tasks, new Ui(), new Storage(temporaryDirectory));

        assertEquals(0, tasks.size());
    }

    private static String captureOutput(Runnable action) {
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream replacementOutput = new PrintStream(
                capturedOutput, true, StandardCharsets.UTF_8)) {
            System.setOut(replacementOutput);
            action.run();
        } finally {
            System.setOut(originalOutput);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
