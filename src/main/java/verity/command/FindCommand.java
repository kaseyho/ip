package verity.command;

import java.util.List;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Represents a command that finds tasks by a description keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a keyword-search command.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        super(false);
        this.keyword = keyword;
    }

    /**
     * Finds and returns tasks whose descriptions contain the keyword.
     *
     * @param tasks Task list to search.
     * @param ui UI used to format matching tasks.
     * @param storage Storage, which is not used.
     * @return User-facing response after execution.
     */
    @Override
    public String execute(
            TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findByKeyword(keyword);
        return ui.getMatchingTasksMessage(matchingTasks);
    }
}
