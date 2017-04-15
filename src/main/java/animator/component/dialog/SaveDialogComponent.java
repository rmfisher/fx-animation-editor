package animator.component.dialog;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import animator.component.util.icon.Svg;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class SaveDialogComponent {

    private static final String FXML_NAME = "DialogComponent.fxml";
    private static final String OVERLAY_STYLE_CLASS = "dialog-background-overlay";

    private final ObjectProperty<Mode> mode = new SimpleObjectProperty<>();
    private final BooleanProperty showing = new SimpleBooleanProperty();
    private final Region overlay = new Region();

    @FXML
    private VBox root;
    @FXML
    private Label dialogLabel;
    @FXML
    private Button leftButton;
    @FXML
    private Button middleButton;
    @FXML
    private Button rightButton;

    private CompletableFuture<Selection> onSelection;
    private Node oldFocusOwner;

    public SaveDialogComponent() {
        loadFxml();
        initUi();
    }

    public Node getRoot() {
        return root;
    }

    public Node getOverlay() {
        return overlay;
    }

    public CompletableFuture<Selection> show(Mode mode) {
        this.mode.set(mode);
        oldFocusOwner = root.getScene().getFocusOwner();
        onSelection = new CompletableFuture<>();
        showing.set(true);
        return onSelection;
    }

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing;
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUi() {
        overlay.getStyleClass().add(OVERLAY_STYLE_CLASS);
        showing.addListener((v, o, n) -> onShowingChanged(n));
        mode.addListener((v, o, n) -> onModeChanged(n));
        root.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        root.visibleProperty().bind(showing);
        root.managedProperty().bind(showing);
        overlay.visibleProperty().bind(showing);
        overlay.managedProperty().bind(showing);
        leftButton.setOnAction(event -> handleSelection(Selection.SAVE));
        middleButton.setOnAction(event -> handleSelection(Selection.CLOSE));
        rightButton.setOnAction(event -> handleSelection(Selection.CANCEL));
        dialogLabel.setGraphic(Svg.WARNING.node());
    }

    private void onShowingChanged(boolean showing) {
        if (showing) {
            if (mode.get() == Mode.CLOSE_FILE_OPEN || mode.get() == Mode.EXIT_FILE_OPEN) {
                leftButton.requestFocus();
            } else {
                middleButton.requestFocus();
            }
        }
    }

    private void onModeChanged(Mode mode) {
        switch (mode) {
            case CLOSE_NO_FILE_OPEN:
                dialogLabel.setText(DialogMessages.CLOSE_DIRTY_NO_FILE_OPEN);
                leftButton.setVisible(false);
                leftButton.setManaged(false);
                middleButton.setText(DialogMessages.DISCARD);
                rightButton.setText(DialogMessages.CANCEL);
                break;
            case CLOSE_FILE_OPEN:
                dialogLabel.setText(DialogMessages.CLOSE_DIRTY_FILE_OPEN);
                leftButton.setVisible(true);
                leftButton.setManaged(true);
                leftButton.setText(DialogMessages.SAVE);
                middleButton.setText(DialogMessages.DISCARD);
                rightButton.setText(DialogMessages.CANCEL);
                break;
            case EXIT_NO_FILE_OPEN:
                dialogLabel.setText(DialogMessages.EXIT_DIRTY_NO_FILE_OPEN);
                leftButton.setVisible(false);
                leftButton.setManaged(false);
                middleButton.setText(DialogMessages.EXIT);
                rightButton.setText(DialogMessages.CANCEL);
                break;
            case EXIT_FILE_OPEN:
                dialogLabel.setText(DialogMessages.EXIT_DIRTY_FILE_OPEN);
                leftButton.setVisible(true);
                leftButton.setManaged(true);
                leftButton.setText(DialogMessages.SAVE_AND_EXIT);
                middleButton.setText(DialogMessages.EXIT);
                rightButton.setText(DialogMessages.CANCEL);
                break;
        }
    }

    private void handleSelection(Selection selection) {
        showing.set(false);
        if (oldFocusOwner != null) {
            oldFocusOwner.requestFocus();
            oldFocusOwner = null;
        }
        if (onSelection != null) {
            onSelection.complete(selection);
            onSelection = null;
        }
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE) {
            handleSelection(Selection.CANCEL);
        } else if (event.getCode() == KeyCode.ENTER) {
            Node focusOwner = root.getScene().getFocusOwner();
            if (focusOwner instanceof Button) {
                ((Button) (focusOwner)).fire();
            }
        }
    }

    public enum Mode {
        CLOSE_NO_FILE_OPEN, CLOSE_FILE_OPEN, EXIT_NO_FILE_OPEN, EXIT_FILE_OPEN
    }

    public enum Selection {
        SAVE, CLOSE, CANCEL
    }
}