package verity.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box containing a message and speaker avatar.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        URL fxmlUrl = Objects.requireNonNull(
                MainWindow.class.getResource("/view/DialogBox.fxml"),
                "Missing DialogBox.fxml resource.");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
            dialog.setText(text);
            displayPicture.setImage(image);
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException(
                    "Could not load DialogBox.fxml.", exception);
        }
    }

    /**
     * Returns a dialog box for a message written by the user.
     *
     * @param text User's message.
     * @param image User's avatar.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(
            String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a flipped dialog box for a response from Verity.
     *
     * @param text Verity's response.
     * @param image Verity's avatar.
     * @return Verity dialog box.
     */
    public static DialogBox getVerityDialog(
            String text, Image image) {
        return getVerityDialog(text, image, null);
    }

    /**
     * Returns a flipped dialog box styled for the command that produced it.
     *
     * @param text Verity's response.
     * @param image Verity's avatar.
     * @param commandType Command that produced the response.
     * @return Verity dialog box.
     */
    public static DialogBox getVerityDialog(
            String text, Image image, String commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /**
     * Places the avatar on the left and the message on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(children);
        getChildren().setAll(children);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Applies a response color based on the command that produced it.
     *
     * @param commandType Command type used to select the response style.
     */
    private void changeDialogStyle(String commandType) {
        if (commandType == null) {
            return;
        }

        switch (commandType) {
        case "AddCommand":
            dialog.getStyleClass().add("add-label");
            break;
        case "MarkCommand":
            dialog.getStyleClass().add("marked-label");
            break;
        case "DeleteCommand":
            dialog.getStyleClass().add("delete-label");
            break;
        default:
            // Do nothing for commands without a dedicated response style.
            break;
        }
    }
}
