package verity.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import verity.task.Task;
import verity.task.TaskList;

/**
 * Loads tasks from and saves tasks to the data file.
 */
public class Storage {
    private final Path dataFilePath;

    /**
     * Creates a storage object that uses the specified data file.
     *
     * @param dataFilePath Path of data file.
     */
    public Storage(Path dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    /**
     * Returns the task lines stored in the data file.
     *
     * @return Task lines, or an empty list if the file does not exist.
     * @throws IOException If the data file exists but cannot be read.
     */
    public List<String> loadTaskLines() throws IOException {
        if (Files.notExists(dataFilePath)) {
            return new ArrayList<>();
        }
        return Files.readAllLines(dataFilePath, StandardCharsets.UTF_8);
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param taskList Tasks to save.
     * @throws IOException If the tasks cannot be saved.
     */
    public void saveTasks(TaskList taskList) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        for (Task task: taskList.getTasks()) {
            fileContents.append(task.serialize())
                    .append(System.lineSeparator());
        }
        writeToFile(fileContents.toString());
    }

    /**
     * Writes the supplied contents to the data file.
     *
     * @param fileContents Contents to write.
     * @throws IOException If the directory or file cannot be written.
     */
    private void writeToFile(String fileContents) throws IOException {
        Path parentDirectory = dataFilePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        Files.writeString(dataFilePath, fileContents, StandardCharsets.UTF_8);
    }
}
