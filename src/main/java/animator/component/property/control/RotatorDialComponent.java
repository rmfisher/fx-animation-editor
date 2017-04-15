package animator.component.property.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class RotatorDialComponent {

    private static final String STYLE_CLASS = "rotator-dial";
    private static final String CIRCLE_STYLE_CLASS = "circle";
    private static final String CIRCLE_PANE_STYLE_CLASS = "circle-pane";
    private static final double CIRCLE_RADIUS = 3;

    private final Button button = new Button();
    private final Circle circle = new Circle(CIRCLE_RADIUS);
    private final StackPane circlePane = new StackPane(circle);
    private final StackPane root = new StackPane(button, circlePane);
    private final DoubleProperty value = new SimpleDoubleProperty();

    private Runnable onChangeStarted;
    private Runnable onChange;
    private Runnable onChangeFinished;

    public RotatorDialComponent() {
        initUi();
        initHandlers();
    }

    public Node getRoot() {
        return root;
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public void setOnChangeStarted(Runnable onChangeStarted) {
        this.onChangeStarted = onChangeStarted;
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public void setOnChangeFinished(Runnable onChangeFinished) {
        this.onChangeFinished = onChangeFinished;
    }

    private void initUi() {
        root.getStyleClass().add(STYLE_CLASS);
        circle.getStyleClass().add(CIRCLE_STYLE_CLASS);
        circlePane.getStyleClass().add(CIRCLE_PANE_STYLE_CLASS);

        circle.setMouseTransparent(true);
        circlePane.setMouseTransparent(true);
        circlePane.setAlignment(Pos.CENTER_RIGHT);
        circlePane.rotateProperty().bind(value);

        button.setFocusTraversable(false);
    }

    private void initHandlers() {
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onRotateStarted);
        button.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::updateRotation);
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> onRotateFinished());
    }

    private void onRotateStarted(MouseEvent event) {
        if (onChangeStarted != null) {
            onChangeStarted.run();
        }
        updateRotation(event);
    }

    private void updateRotation(MouseEvent event) {
        if (!value.isBound()) {
            double mouseToCenterX = event.getX() - button.getWidth() / 2;
            double mouseToCenterY = event.getY() -  button.getHeight() / 2;
            double angle = Math.round(Math.toDegrees(Math.atan2(mouseToCenterY, mouseToCenterX)));
            double positiveAngle = angle < 0 ? angle + 360 : angle;
            setValue(positiveAngle);
            if (onChange != null) {
                onChange.run();
            }
        }
    }

    private void onRotateFinished() {
        if (onChangeFinished != null) {
            onChangeFinished.run();
        }
    }
}
