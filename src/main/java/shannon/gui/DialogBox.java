package shannon.gui;

import java.io.IOException;
import java.util.Collections;

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
 * One speech bubble in the conversation: a picture of the speaker beside what they said.
 * <p>
 * The layout comes from {@code /view/DialogBox.fxml}, which uses the {@code fx:root} construct.
 * That is what allows this class to <em>be</em> the {@link HBox} the FXML describes rather than
 * merely contain one, so a dialog box can be dropped straight into the conversation.
 * <p>
 * The constructor is private; callers use {@link #getUserDialog} or {@link #getShannonDialog}
 * instead. Naming the two cases makes it impossible to forget to flip Shannon's bubble, and
 * reads better at the call site than a boolean argument would.
 */
public class DialogBox extends HBox {

    /** What was said. Filled in by the FXML loader, matched by the {@code fx:id} in the file. */
    @FXML
    private Label dialog;

    /** The speaker's avatar. Also filled in by the FXML loader. */
    @FXML
    private ImageView displayPicture;

    /**
     * Builds one bubble from the FXML layout.
     *
     * @param text what the speaker said.
     * @param image the speaker's avatar.
     */
    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            // setRoot before load, so the loader fills in this object instead of making a new
            // HBox; setController so the @FXML fields above are the ones it injects into.
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // A missing or malformed FXML file is a packaging mistake, not something the user
            // can act on, so it is reported to the console rather than shown in the window.
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /**
     * Returns a bubble for something the user said, with the picture on the right.
     *
     * @param text what the user typed.
     * @param image the user's avatar.
     * @return the bubble, ready to be added to the conversation.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a bubble for something Shannon said, mirrored so that the two speakers sit on
     * opposite sides of the window and are easy to tell apart at a glance.
     *
     * @param text Shannon's reply.
     * @param image Shannon's avatar.
     * @return the bubble, ready to be added to the conversation.
     */
    public static DialogBox getShannonDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Swaps the picture and the text, so the picture ends up on the left.
     * The children are copied out, reversed, and put back, because an observable list cannot be
     * reordered while it is being read.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
