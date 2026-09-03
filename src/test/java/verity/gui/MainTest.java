package verity.gui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Tests resources required by the JavaFX application.
 */
class MainTest {

    @Test
    void imageResources_applicationStarts_resourcesAreAvailable() {
        assertAll(
                () -> assertNotNull(
                        Main.class.getResource(
                                "/images/verity_user.png")),
                () -> assertNotNull(
                        Main.class.getResource(
                                "/images/verity_bot.png"))
        );
    }

    @Test
    void fxmlViews_arePackagedWithApplication() {
        assertAll(
                () -> assertNotNull(
                        Main.class.getResource(
                                "/view/MainWindow.fxml")),
                () -> assertNotNull(
                        Main.class.getResource(
                                "/view/DialogBox.fxml"))
        );
    }
}
