package verity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

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
        TaskList tasks = new TaskList(List.of(todo));
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        new DeleteCommand(0).execute(
                tasks, new Ui(), new Storage(dataFile));

        assertEquals(0, tasks.size());
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    void markCommand_execute_marksTaskAndSavesIt() throws IOException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));
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
        TaskList tasks = new TaskList(List.of(todo));
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        new UnmarkCommand(0).execute(
                tasks, new Ui(), new Storage(dataFile));

        assertEquals("[T][ ] read book", tasks.get(0).getStatus());
        assertEquals(
                "T\t0\tread book" + System.lineSeparator(),
                Files.readString(dataFile));
    }

    @Test
    void listCommand_execute_returnsAllTasks() {
        TaskList tasks = new TaskList(
                List.of(new Todo("read book")));

        String response = new ListCommand().execute(
                tasks, new Ui(), new Storage(temporaryDirectory));

        assertTrue(response.contains("1.[T][ ] read book"));
    }

    @Test
    void findCommand_execute_returnsOnlyKeywordMatches() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("submit report"),
                new Todo("return BOOK")
        ));

        String response = new FindCommand("book").execute(
                tasks, new Ui(), new Storage(temporaryDirectory));

        assertTrue(response.contains("1.[T][ ] read book"));
        assertTrue(response.contains("2.[T][ ] return BOOK"));
        assertFalse(response.contains("[T][ ] submit report"));
    }

    @Test
    void findDateCommand_execute_returnsTasksOnDate() {
        LocalDate date = LocalDate.of(2026, 8, 10);
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit report", date)
        ));

        String response = new FindDateCommand(date).execute(
                tasks, new Ui(), new Storage(temporaryDirectory));

        assertTrue(response.contains("Tasks on 2026-08-10:"));
        assertTrue(response.contains("[D][ ] submit report"));
        assertFalse(response.contains("[T][ ] read book"));
    }

    @Test
    void exitCommand_execute_returnsExitMessageWithoutChangingTasks() {
        TaskList tasks = new TaskList();

        String response = new ExitCommand().execute(
                tasks, new Ui(), new Storage(temporaryDirectory));

        assertTrue(response.contains("Bye. Hope to see you again soon!"));
        assertEquals(0, tasks.size());
    }
}
