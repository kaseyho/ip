package verity.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import verity.task.Deadline;
import verity.task.TaskList;
import verity.task.Todo;

/**
 * Tests user input and responses formatted by the UI.
 */
class UiTest {
    private static final String HORIZONTAL_LINE =
            "____________________________________________________________\n";

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
    void getGreeting_returnsWelcomeMessage() {
        String response = new Ui().getGreeting();

        assertTrue(response.contains("Hello! I'm Verity."));
        assertTrue(response.contains("What can I do for you?"));
    }

    @Test
    void getExitMessage_returnsGoodbyeMessage() {
        String response = new Ui().getExitMessage();

        assertTrue(response.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void getTaskAddedMessage_returnsTaskAndCount() {
        String response = new Ui().getTaskAddedMessage(
                new Todo("read book"), 1);

        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 1 tasks"));
    }

    @Test
    void getTaskMarkedMessage_returnsCompletedTask() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        String response = new Ui().getTaskMarkedMessage(todo);

        assertEquals(
                HORIZONTAL_LINE + "\n"
                        + "Nice! I've marked this task as done:\n\n"
                        + "[T][X] read book\n"
                        + HORIZONTAL_LINE,
                response);
    }

    @Test
    void getTaskUnmarkedMessage_returnsIncompleteTask() {
        Todo todo = new Todo("read book");

        String response = new Ui().getTaskUnmarkedMessage(todo);

        assertEquals(
                HORIZONTAL_LINE + "\n"
                        + "Ok, I've marked this task as not done yet:\n\n"
                        + "[T][ ] read book\n"
                        + HORIZONTAL_LINE,
                response);
    }

    @Test
    void getTaskDeletedMessage_returnsTaskAndRemainingCount() {
        String response = new Ui().getTaskDeletedMessage(
                new Todo("read book"), 0);

        assertEquals(
                HORIZONTAL_LINE + "\n"
                        + "Noted. I've removed this task:\n\n"
                        + "[T][ ] read book\n"
                        + HORIZONTAL_LINE + "\n"
                        + "Now you have 0 tasks in the list.\n\n"
                        + HORIZONTAL_LINE,
                response);
    }

    @Test
    void getTaskListMessage_returnsNumberedTasks() {
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", LocalDate.of(2026, 8, 10))
        );

        String response = new Ui().getTaskListMessage(tasks);

        assertEquals(
                HORIZONTAL_LINE + "\n"
                        + "    Here are the tasks in your list:\n\n"
                        + "    1.[T][ ] read book\n"
                        + "    2.[D][ ] submit report "
                        + "(by: Aug 10 2026)\n"
                        + HORIZONTAL_LINE,
                response);
    }

    @Test
    void getTasksOnMessage_matchingTasks_returnsDateAndTasks() {
        LocalDate date = LocalDate.of(2026, 8, 10);
        String response = new Ui().getTasksOnMessage(
                date,
                List.of(new Deadline("submit report", date)));

        assertTrue(response.contains("Tasks on 2026-08-10:"));
        assertTrue(response.contains("[D][ ] submit report"));
    }

    @Test
    void getTasksOnMessage_noMatchingTasks_returnsEmptyMessage() {
        String response = new Ui().getTasksOnMessage(
                LocalDate.of(2026, 8, 10), List.of());

        assertTrue(response.contains("There are no tasks on this date."));
    }

    @Test
    void getMatchingTasksMessage_matchingTasks_returnsNumberedTasks() {
        String response = new Ui().getMatchingTasksMessage(List.of(
                new Todo("read book"),
                new Todo("return book")));

        assertTrue(response.contains(
                "Here are the matching tasks in your list:"));
        assertTrue(response.contains("1.[T][ ] read book"));
        assertTrue(response.contains("2.[T][ ] return book"));
    }

    @Test
    void getMatchingTasksMessage_noMatchingTasks_returnsEmptyMessage() {
        String response = new Ui().getMatchingTasksMessage(List.of());

        assertTrue(response.contains("There are no matching tasks."));
    }

    @Test
    void errorMessageMethods_returnExpectedMessages() {
        Ui ui = new Ui();

        String commandError =
                ui.getCommandErrorMessage("invalid command");
        String loadingError = ui.getLoadingErrorMessage();
        String corruptedDataError =
                ui.getCorruptedDataErrorMessage("Line 1 is invalid.");
        String savingError = ui.getSavingErrorMessage();

        assertTrue(commandError.contains(
                "Speak your truth. invalid command"));
        assertTrue(loadingError.contains(
                "I could not load your saved tasks."));
        assertTrue(corruptedDataError.contains(
                "The saved task data is corrupted."));
        assertTrue(corruptedDataError.contains("Line 1 is invalid."));
        assertTrue(savingError.contains(
                "I could not save your tasks."));
    }
}
