package verity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the chatbot's top-level startup and command loop behaviour.
 */
class VerityTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void run_missingDataFile_startsAndExitsNormally() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        String output = runWithInput(dataFile, "bye\n");

        assertTrue(output.contains("Hello! I'm Verity."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void run_existingDataFile_loadsAndListsSavedTask() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                "T\t0\tread book" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        String output = runWithInput(dataFile, "list\nbye\n");

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void run_invalidCommand_showsErrorAndContinues() {
        String output = runWithInput(
                temporaryDirectory.resolve("tasks.txt"),
                "unknown\nbye\n");

        assertTrue(output.contains("I don't know that command."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void run_corruptedData_showsCorruptionErrorAndStops() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                "X\t0\tunknown task" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        String output = runWithInput(dataFile, "bye\n");

        assertTrue(output.contains("The saved task data is corrupted."));
        assertFalse(output.contains("Bye. Hope to see you again soon!"));
    }

    private static String runWithInput(Path dataFile, String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream replacementOutput = new PrintStream(
                capturedOutput, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(
                    input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(replacementOutput);
            new Verity(dataFile).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
