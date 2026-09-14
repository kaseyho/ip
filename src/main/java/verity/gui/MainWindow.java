package verity.gui;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import verity.Verity;

/**
 * Controls the main Verity GUI window.
 */
public class MainWindow {
    private static final Duration EXIT_DELAY = Duration.millis(750);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private VBox emptyState;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final BooleanProperty isClosing = new SimpleBooleanProperty(false);
    private Verity verity;

    /**
     * Configures input validation and window controls after FXML loading.
     */
    @FXML
    public void initialize() {
        BooleanBinding isBlankInput = Bindings.createBooleanBinding(
                () -> userInput.getText().isBlank(),
                userInput.textProperty());
        sendButton.disableProperty().bind(isBlankInput.or(isClosing));
        userInput.disableProperty().bind(isClosing);
    }

    /**
     * Supplies the Verity instance used to process user commands.
     *
     * @param verity Verity instance for this window.
     */
    public void setVerity(Verity verity) {
        this.verity = Objects.requireNonNull(verity,
                "Verity instance must not be null.");
    }

    /**
     * Displays the user's input and Verity's response.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (isClosing.get() || input.isBlank()) {
            return;
        }

        String response = verity.getResponse(input);
        String commandType = verity.getCommandType();
        boolean isError = isErrorResponse(commandType);

        hideEmptyState();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getVerityDialog(
                        formatResponseForGui(response), isError));
        Platform.runLater(() -> scrollPane.setVvalue(1.0));

        userInput.clear();
        userInput.requestFocus();

        if (verity.isExitRequested()) {
            closeAfterFarewell();
        }
    }

    static boolean isErrorResponse(String commandType) {
        return commandType == null;
    }

    static String formatResponseForGui(String response) {
        return response.replaceAll("(?m)^_+\\R", "").trim();
    }

    private void hideEmptyState() {
        emptyState.setVisible(false);
        emptyState.setManaged(false);
    }

    private void closeAfterFarewell() {
        isClosing.set(true);
        PauseTransition exitDelay = new PauseTransition(EXIT_DELAY);
        exitDelay.setOnFinished(event -> Platform.exit());
        exitDelay.play();
    }
}
