package verity.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores and manages the user's tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param initialTasks Tasks to place in the task list.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns a task.
     *
     * @param taskIndex Zero-based index of the task.
     * @return Deleted task.
     */
    public Task delete(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Returns a task.
     *
     * @param taskIndex Zero-based index of the task.
     * @return Task at the specified index.
     */
    public Task get(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Marks a task as completed.
     *
     * @param taskIndex Zero-based index of the task.
     * @return Updated task.
     */
    public Task mark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as incomplete.
     *
     * @param taskIndex Zero-based index of the task.
     * @return Updated task.
     */
    public Task unmark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsUndone();
        return task;
    }

    /**
     * Returns tasks occurring on a date.
     *
     * @param date Date to search for.
     * @return Tasks occurring on the date.
     */
    public List<Task> findOn(LocalDate date) {
        List<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

    /**
     * Returns tasks whose descriptions contain the keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original order.
     */
    public List<Task> findByKeyword(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (task.matchesKeyword(keyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the number of tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only snapshot of the tasks.
     *
     * @return Snapshot of the tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
