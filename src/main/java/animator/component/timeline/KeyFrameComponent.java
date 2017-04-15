package animator.component.timeline;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import animator.command.CommandStack;
import animator.command.TimeChangeCommand;
import animator.component.util.icon.Svg;
import animator.model.KeyFrameModel;

import java.io.IOException;

public class KeyFrameComponent {

    private static final String FXML_NAME = "KeyFrameComponent.fxml";
    private static final String ARROW_STYLE_CLASS = "arrow";
    private static final String TIME_CHANGE_LISTENER_KEY = "time.change.listener";
    private static final double MIN_VALUE = 0.01;
    private static final double INCREMENT_VALUE = 0.05;

    private final KeyFrameModel model;
    private final CommandStack commandStack;

    @FXML
    private Pane root;
    @FXML
    private HBox leftButtonBox;
    @FXML
    private HBox rightButtonBox;
    @FXML
    private Button deleteButton;
    @FXML
    private Button incrementButton;
    @FXML
    private Button decrementButton;
    @FXML
    private HBox labelBox;
    @FXML
    private Label plusLabel;
    @FXML
    private KeyFrameTimeLabel timeLabel;
    @FXML
    private Region highlight;

    public KeyFrameComponent(KeyFrameModel model, CommandStack commandStack) {
        this.model = model;
        this.commandStack = commandStack;
        loadFxml();
        initUi();
        addEventHandlers();
    }

    public Node getRoot() {
        return root;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initUi() {
        timeLabel.setValue(model.getTime());

        // Use weak listener here so view can be garbage collected when key frame is deleted, because model instance will hang around in command stack.
        ChangeListener<Number> timeChangeListener = (v, o, n) -> timeLabel.setValue(n.doubleValue());
        timeLabel.getProperties().put(TIME_CHANGE_LISTENER_KEY, timeChangeListener);
        model.timeProperty().addListener(new WeakChangeListener<>(timeChangeListener));

        incrementButton.setGraphic(createArrowGraphic(NodeOrientation.LEFT_TO_RIGHT));
        decrementButton.setGraphic(createArrowGraphic(NodeOrientation.RIGHT_TO_LEFT));
        deleteButton.setGraphic(Svg.MINUS_TINY.node());

        leftButtonBox.visibleProperty().bind(model.timeProperty().greaterThan(0));
        rightButtonBox.visibleProperty().bind(model.timeProperty().greaterThan(0));
        plusLabel.visibleProperty().bind(model.timeProperty().greaterThan(0));
        plusLabel.managedProperty().bind(plusLabel.visibleProperty());

        highlight.visibleProperty().bind(model.selectedProperty());
    }

    private Node createArrowGraphic(NodeOrientation direction) {
        StackPane arrow = new StackPane();
        arrow.getStyleClass().add(ARROW_STYLE_CLASS);
        arrow.setNodeOrientation(direction);
        return arrow;
    }

    private void addEventHandlers() {
        labelBox.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onLabelBoxClick);
        incrementButton.setOnAction(event -> adjustTime(INCREMENT_VALUE));
        decrementButton.setOnAction(event -> adjustTime(-INCREMENT_VALUE));
        timeLabel.setOnCommit(this::updateModel);
    }

    private void onLabelBoxClick(MouseEvent event) {
        if (event.getClickCount() == 2 && model.getTime() > 0) {
            timeLabel.setEditMode(true);
        }
    }

    private void adjustTime(double adjustment) {
        timeLabel.setEditMode(false); // If we are in edit mode the label's value will be bound, so we need to stop editing before updating the model.
        double rounded = Math.rint((model.getTime() + adjustment) / INCREMENT_VALUE) * INCREMENT_VALUE;
        double roundedTo2dp = Math.rint(rounded * 100) / 100.0;
        timeLabel.setValue(Math.max(MIN_VALUE, roundedTo2dp));
        updateModel();
    }

    private void updateModel() {
        if (Double.compare(timeLabel.getValue(), model.getTime()) != 0) {
            commandStack.appendAndExecute(new TimeChangeCommand(model, model.getTime(), timeLabel.getValue()));
        }
    }
}
