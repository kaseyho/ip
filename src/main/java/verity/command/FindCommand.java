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
     * Finds and displays tasks whose descriptions contain the keyword.
     *
     * @param tasks verity.task.Task list to search.
     * @param ui UI used to display matching tasks.
     * @param storage verity.storage.Storage, which is not used.
     */
    @Override
    public void execute(
            TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.findByKeyword(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
