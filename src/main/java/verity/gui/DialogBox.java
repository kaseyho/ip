package verity.gui;

import javafx.geometry.Pos;
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

    /**
     * Creates a dialog box with the specified message and avatar.
     *
     * @param text Message to display.
     * @param image Speaker avatar to display.
     */
    public DialogBox(String text, Image image) {
        dialog = new Label(text);
        displayPicture = new ImageView(image);

        dialog.setWrapText(true);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);
        displayPicture.setPreserveRatio(true);
        setAlignment(Pos.TOP_RIGHT);

        getChildren().addAll(dialog, displayPicture);
    }
}