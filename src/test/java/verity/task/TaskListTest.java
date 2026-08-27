package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection operations.
 */
class TaskListTest {
    private static final LocalDate MATCHING_DATE =
            LocalDate.of(2026, 8, 10);

    @Test
    void emptyTaskList_hasZeroSizeAndNoTasks() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
        assertTrue(taskList.getTasks().isEmpty());
    }

    @Test
    void add_task_increasesSizeAndMakesTaskAccessible() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");

        taskList.add(todo);

        assertEquals(1, taskList.size());
        assertSame(todo, taskList.get(0));
    }

    @Test
    void delete_existingTask_returnsTaskAndRemovesIt() {
        Todo todo = new Todo("read book");
        TaskList taskList = new TaskList(List.of(todo));

        Task deletedTask = taskList.delete(0);

        assertSame(todo, deletedTask);
        assertEquals(0, taskList.size());
    }

    @Test
    void mark_task_marksTaskAsDoneAndReturnsIt() {
        Todo todo = new Todo("read book");
        TaskList taskList = new TaskList(List.of(todo));

        Task markedTask = taskList.mark(0);

        assertSame(todo, markedTask);
        assertEquals("[T][X] read book", todo.getStatus());
    }

    @Test
    void unmark_completedTask_marksTaskAsUndoneAndReturnsIt() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList taskList = new TaskList(List.of(todo));

        Task unmarkedTask = taskList.unmark(0);

        assertSame(todo, unmarkedTask);
        assertEquals("[T][ ] read book", todo.getStatus());
    }

    @Test
    void findOn_date_returnsMatchingDeadlinesAndEventsInOrder() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", MATCHING_DATE);
        Event event = new Event(
                "project meeting",
                MATCHING_DATE.minusDays(1),
                MATCHING_DATE.plusDays(1)
        );
        TaskList taskList = new TaskList(List.of(todo, deadline, event));

        List<Task> matchingTasks = taskList.findOn(MATCHING_DATE);

        assertEquals(List.of(deadline, event), matchingTasks);
    }

    @Test
    void findOn_dateWithNoMatches_returnsEmptyList() {
        TaskList taskList = new TaskList(
                List.of(new Todo("read book")));

        assertTrue(taskList.findOn(MATCHING_DATE).isEmpty());
    }

    @Test
    void findByKeyword_mixedCaseKeyword_returnsMatchesInOriginalOrder() {
        Todo firstMatch = new Todo("read book");
        Deadline nonMatch = new Deadline("submit report", MATCHING_DATE);
        Todo secondMatch = new Todo("return BOOK");
        TaskList taskList = new TaskList(
                List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matchingTasks = taskList.findByKeyword("BoOk");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void findByKeyword_keywordWithNoMatches_returnsEmptyList() {
        TaskList taskList = new TaskList(
                List.of(new Todo("read book")));

        assertTrue(taskList.findByKeyword("report").isEmpty());
    }

    @Test
    void constructor_initialListIsCopied() {
        List<Task> initialTasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        initialTasks.add(todo);

        TaskList taskList = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, taskList.size());
        assertSame(todo, taskList.get(0));
    }

    @Test
    void getTasks_returnsReadOnlySnapshot() {
        TaskList taskList = new TaskList(
                List.of(new Todo("read book")));

        List<Task> snapshot = taskList.getTasks();

        assertThrows(
                UnsupportedOperationException.class,
                () -> snapshot.add(new Todo("write book"))
        );
        assertFalse(snapshot.isEmpty());
    }
}
