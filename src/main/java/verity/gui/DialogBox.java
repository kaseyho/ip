package verity.gui;

import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private final Label dialog;
    private final ImageView displayPicture;

    private DialogBox(String text, Image image) {
        dialog = new Label(text);
        displayPicture = new ImageView(image);

        dialog.setWrapText(true);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);
        displayPicture.setPreserveRatio(true);
        setAlignment(Pos.TOP_RIGHT);

        getChildren().addAll(dialog, displayPicture);
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
        ObservableList<Node> children =
                FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
