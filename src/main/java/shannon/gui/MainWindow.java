package shannon.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import shannon.Shannon;

/**
 * The behavior behind {@code /view/MainWindow.fxml}: what happens when the user types.
 * <p>
 * This is a "controller". The FXML file says what the window looks like; this class says what
 * it does. The two are joined by name: each {@code @FXML} field below matches an {@code fx:id}
 * in the file, and {@link #handleUserInput()} matches the {@code onAction="#handleUserInput"}
 * on the text field and the button.
 */
public class MainWindow extends AnchorPane {

    /** How long the window stays open after {@code bye}, so the goodbye can be read. */
    private static final double GOODBYE_DELAY_SECONDS = 1.5;

    /** The scrolling area holding the conversation. */
    @FXML
    private ScrollPane scrollPane;

    /** The column of dialog boxes inside {@link #scrollPane}. */
    @FXML
    private VBox dialogContainer;

    /** Where the user types a command. */
    @FXML
    private TextField userInput;

    /** The Send button, an alternative to pressing Enter. */
    @FXML
    private Button sendButton;

    /** The chatbot itself. Handed over by {@link Main} through {@link #setShannon}. */
    private Shannon shannon;

    /** The user's avatar, loaded once and reused by every bubble the user sends. */
    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));

    /** Shannon's avatar, likewise reused. Still the tutorial's file name, hence "DaDuke". */
    private final Image shannonImage =
            new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /**
     * Prepares the window once the FXML has been loaded and the fields above are filled in.
     * <p>
     * JavaFX calls this automatically; it cannot be done in a constructor, because at that point
     * the {@code @FXML} fields are still null. Binding the scroll position to the height of the
     * conversation is what keeps the newest message in view as the list grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Hands the chatbot to the window and shows its greeting.
     * <p>
     * The chatbot is passed in rather than created here, so that the window stays a window: it
     * knows how to display a conversation, not which chatbot it is having one with.
     *
     * @param shannon the chatbot that will answer the user.
     */
    public void setShannon(Shannon shannon) {
        this.shannon = shannon;
        dialogContainer.getChildren().add(
                DialogBox.getShannonDialog(shannon.getStartupMessage(), shannonImage));
    }

    /**
     * Answers whatever the user has typed, by adding their words and Shannon's reply to the
     * conversation and then clearing the text field.
     * <p>
     * Called by JavaFX when the user presses Enter in the text field or clicks Send.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = shannon.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getShannonDialog(response, shannonImage)
        );
        userInput.clear();

        if (shannon.isExitCommand(input)) {
            closeAfterGoodbye();
        }
    }

    /**
     * Closes the window a moment after {@code bye}, so the goodbye can actually be read.
     * Closing immediately would make the reply flash past unseen.
     */
    private void closeAfterGoodbye() {
        PauseTransition pause = new PauseTransition(Duration.seconds(GOODBYE_DELAY_SECONDS));
        pause.setOnFinished(event -> Platform.exit());
        pause.play();
    }
}
