package verity.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import verity.task.Deadline;
import verity.task.TaskList;
import verity.task.Todo;

/**
 * Tests user input and output handled by the UI.
 */
class UiTest {
    @Test
    void readCommand_inputLine_returnsLine() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("list\n".getBytes(
                    StandardCharsets.UTF_8)));
            Ui ui = new Ui();

            assertEquals("list", ui.readCommand());
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    void showGreeting_printsWelcomeMessage() {
        String output = captureOutput(() -> new Ui().showGreeting());

        assertTrue(output.contains("Hello! I'm Verity."));
        assertTrue(output.contains("What can I do for you?"));
    }

    @Test
    void showExit_printsGoodbyeMessage() {
        String output = captureOutput(() -> new Ui().showExit());

        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void showTaskAdded_printsTaskAndCount() {
        String output = captureOutput(
                () -> new Ui().showTaskAdded(new Todo("read book"), 1));

        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("Now you have 1 tasks"));
    }

    @Test
    void showTaskMarked_printsCompletedTask() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        String output = captureOutput(
                () -> new Ui().showTaskMarked(todo));

        assertTrue(output.contains("marked this task as done"));
        assertTrue(output.contains("[T][X] read book"));
    }

    @Test
    void showTaskUnmarked_printsIncompleteTask() {
        Todo todo = new Todo("read book");

        String output = captureOutput(
                () -> new Ui().showTaskUnmarked(todo));

        assertTrue(output.contains("marked this task as not done yet"));
        assertTrue(output.contains("[T][ ] read book"));
    }

    @Test
    void showTaskDeleted_printsTaskAndRemainingCount() {
        String output = captureOutput(
                () -> new Ui().showTaskDeleted(new Todo("read book"), 0));

        assertTrue(output.contains("removed this task"));
        assertTrue(output.contains("Now you have 0 tasks"));
    }

    @Test
    void showTaskList_printsNumberedTasks() {
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", LocalDate.of(2026, 8, 10))
        );

        String output = captureOutput(
                () -> new Ui().showTaskList(tasks));

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][ ] submit report"));
    }

    @Test
    void showTasksOn_matchingTasks_printsDateAndTasks() {
        LocalDate date = LocalDate.of(2026, 8, 10);
        String output = captureOutput(
                () -> new Ui().showTasksOn(
                        date,
                        List.of(new Deadline("submit report", date))));

        assertTrue(output.contains("Tasks on 2026-08-10:"));
        assertTrue(output.contains("[D][ ] submit report"));
    }

    @Test
    void showTasksOn_noMatchingTasks_printsEmptyMessage() {
        String output = captureOutput(
                () -> new Ui().showTasksOn(
                        LocalDate.of(2026, 8, 10), List.of()));

        assertTrue(output.contains("There are no tasks on this date."));
    }

    @Test
    void showMatchingTasks_matchingTasks_printsNumberedTasks() {
        String output = captureOutput(
                () -> new Ui().showMatchingTasks(List.of(
                        new Todo("read book"),
                        new Todo("return book"))));

        assertTrue(output.contains(
                "Here are the matching tasks in your list:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] return book"));
    }

    @Test
    void showMatchingTasks_noMatchingTasks_printsEmptyMessage() {
        String output = captureOutput(
                () -> new Ui().showMatchingTasks(List.of()));

        assertTrue(output.contains("There are no matching tasks."));
    }

    @Test
    void showErrorMethods_printExpectedErrorMessages() {
        String output = captureOutput(() -> {
            Ui ui = new Ui();
            ui.showCommandError("invalid command");
            ui.showLoadingError();
            ui.showCorruptedDataError("Line 1 is invalid.");
            ui.showSavingError();
        });

        assertTrue(output.contains("Speak your truth. invalid command"));
        assertTrue(output.contains("I could not load your saved tasks."));
        assertTrue(output.contains("The saved task data is corrupted."));
        assertTrue(output.contains("Line 1 is invalid."));
        assertTrue(output.contains("I could not save your tasks."));
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
