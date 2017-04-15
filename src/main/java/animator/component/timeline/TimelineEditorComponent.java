package animator.component.timeline;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import animator.component.util.FocusHelper;
import animator.component.util.icon.Svg;

import javax.inject.Inject;
import java.io.IOException;

public class TimelineEditorComponent {

    private static final String FXML_NAME = "TimelineEditorComponent.fxml";

    private final TimelineEditorPresenter presenter;

    @FXML
    private VBox root;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Node scrollPaneContent;
    @FXML
    private HBox keyFramesBox;
    @FXML
    private Button addButton;

    private ChangeListener<Number> listenerForcingScrollToEnd;

    @Inject
    public TimelineEditorComponent(TimelineEditorPresenter presenter) {
        this.presenter = presenter;
        loadFxml();
        initUi();
        initScrollValueListener();
        initPresenter();
    }

    private void initScrollValueListener() {
        listenerForcingScrollToEnd = (v, o, n) -> {
            if (n.doubleValue() < 1) {
                scrollPane.setHvalue(1);
                Platform.runLater(() -> scrollPane.hvalueProperty().removeListener(listenerForcingScrollToEnd));
            }
        };
    }

    public Node getRoot() {
        return root;
    }

    Node getScrollPaneContent() {
        return scrollPaneContent;
    }

    HBox getKeyFramesBox() {
        return keyFramesBox;
    }

    Button getAddButton() {
        return addButton;
    }

    void scrollToEnd() {
        scrollPane.setHvalue(1);
        // The scroll pane will try to readjust the value during the next layout pass so we add a listener temporarily to switch it back to 1.
        scrollPane.hvalueProperty().addListener(listenerForcingScrollToEnd);
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException ignored) {
        }
    }

    private void initUi() {
        addButton.setGraphic(Svg.PLUS.node());
        scrollPane.setFocusTraversable(false);
        FocusHelper.requestFocusOnPress(root);
        FocusHelper.redirectFocusOnPress(addButton, root);
    }

    private void initPresenter() {
        presenter.setView(this);
    }
}
