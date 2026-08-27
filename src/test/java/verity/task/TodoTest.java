package verity.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the observable behavior of todo tasks.
 */
class TodoTest {
    private static final LocalDate ANY_DATE = LocalDate.of(2026, 8, 10);

    @Test
    void getStatus_incompleteTodo_includesTodoTypeAndDescription() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.getStatus());
    }

    @Test
    void getStatus_completedTodo_showsCompletedStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.getStatus());
    }

    @Test
    void serialize_incompleteTodo_returnsStorageFormat() {
        Todo todo = new Todo("read book");

        assertEquals("T\t0\tread book", todo.serialize());
    }

    @Test
    void serialize_completedTodo_returnsCompletedStorageFormat() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("T\t1\tread book", todo.serialize());
    }

    @Test
    void occursOn_anyDate_returnsFalse() {
        Todo todo = new Todo("read book");

        assertFalse(todo.occursOn(ANY_DATE));
    }
}
