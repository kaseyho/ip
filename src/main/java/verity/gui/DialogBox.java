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
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
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
    }
}
