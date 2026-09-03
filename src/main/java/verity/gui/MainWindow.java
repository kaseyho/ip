package verity.gui;

import java.io.InputStream;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import verity.Verity;

/**
 * Controller for the main Verity GUI window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = loadImage(
            "/images/verity_user.png", "Missing user avatar resource.");
    private final Image verityImage = loadImage(
            "/images/verity_bot.png", "Missing Verity avatar resource.");

    private Verity verity;

    /**
     * Configures the window to keep the latest dialog visible.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(
                dialogContainer.heightProperty());
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
        String response = verity.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getVerityDialog(response, verityImage));
        userInput.clear();
    }

    /**
     * Loads an image resource and reports a descriptive error if it is absent
     * or malformed.
     *
     * @param resourcePath Classpath path of the image.
     * @param errorMessage Error message for a missing image.
     * @return Loaded image.
     */
    private static Image loadImage(
            String resourcePath, String errorMessage) {
        InputStream imageStream = Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                errorMessage);
        Image image = new Image(imageStream);
        if (image.isError()) {
            Throwable cause = image.getException();
            String causeMessage = cause == null
                    ? ""
                    : " " + cause;
            throw new IllegalStateException(
                    "Invalid image resource " + resourcePath + "."
                            + causeMessage,
                    cause);
        }
        return image;
    }
}
