package animator.component.scene.selection;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import animator.component.scene.panning.PanningComponent;

import java.util.function.Consumer;

public class DragBehavior {

    private static final double DRAG_INERTIA = 3;
    private static final double GRID_SIZE = 10;

    private final Timeline timelineX = new Timeline();
    private final Timeline timelineY = new Timeline();
    private final DoubleProperty animatedPanX = new SimpleDoubleProperty();
    private final DoubleProperty animatedPanY = new SimpleDoubleProperty();
    private final BooleanProperty snapToGrid = new SimpleBooleanProperty();
    private final EventHandler<MouseEvent> onMousePressed = this::onMousePressed;
    private final EventHandler<MouseEvent> onMouseDragged = this::onMouseDragged;
    private final EventHandler<MouseEvent> onMouseReleased = this::onMouseReleased;

    private Runnable onDragStarted;
    private Consumer<Point2D> onDragged;
    private Runnable onDragDone;

    private Node node;
    private PanningComponent panningComponent;
    private double xAtPress;
    private double yAtPress;
    private double screenXAtPress;
    private double screenYAtPress;
    private double lastScreenX;
    private double lastScreenY;
    private double panXAtPress;
    private double panYAtPress;
    private boolean dragActive;
    private boolean inertiaOvercome;

    public DragBehavior() {
        animatedPanX.addListener((v, o, n) -> onAnimatedPanChanged(animatedPanX, panningComponent.panXProperty()));
        animatedPanY.addListener((v, o, n) -> onAnimatedPanChanged(animatedPanY, panningComponent.panYProperty()));
    }

    public void apply(Node node, PanningComponent panningComponent) {
        this.node = node;
        this.panningComponent = panningComponent;
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressed);
        node.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDragged);
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleased);
    }

    public void dispose(Node node) {
        node.removeEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressed);
        node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDragged);
        node.removeEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleased);
        this.node = null;
        this.panningComponent = null;
    }

    public void setOnDragStarted(Runnable onDragStarted) {
        this.onDragStarted = onDragStarted;
    }

    public void setOnDragged(Consumer<Point2D> onDragged) {
        this.onDragged = onDragged;
    }

    public void setOnDragDone(Runnable onDragDone) {
        this.onDragDone = onDragDone;
    }

    BooleanProperty snapToGridProperty() {
        return snapToGrid;
    }

    private void onMousePressed(MouseEvent event) {
        // Don't start drag if shortcut is down because the node might be about to be deselected.
        if (event.getButton() == MouseButton.PRIMARY && !event.isShortcutDown()) {
            screenXAtPress = event.getScreenX();
            screenYAtPress = event.getScreenY();
            lastScreenX = screenXAtPress;
            lastScreenY = screenYAtPress;
            panXAtPress = panningComponent.panXProperty().get();
            panYAtPress = panningComponent.panYProperty().get();
            xAtPress = node.getLayoutX();
            yAtPress = node.getLayoutY();
            dragActive = true;
            inertiaOvercome = false;
            if (onDragStarted != null) {
                onDragStarted.run();
            }
            event.consume();
        }
    }

    private void onMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (dragActive && (inertiaOvercome || isDraggedFarEnoughToOvercomeInertia(event.getScreenX(), event.getScreenY()))) {
                inertiaOvercome = true;
                lastScreenX = event.getScreenX();
                lastScreenY = event.getScreenY();
                if (onDragged != null) {
                    onDragged.accept(calculateDragDelta());
                }
                panIfOutsideAxisBounds(event);
            }
            event.consume();
        }
    }

    private void onMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            dragActive = false;
            if (onDragDone != null) {
                onDragDone.run();
            }
            timelineX.stop();
            timelineY.stop();
            event.consume();
        }
    }

    private Point2D calculateDragDelta() {
        double deltaXInScreen = lastScreenX - screenXAtPress;
        double deltaYInScreen = lastScreenY - screenYAtPress;
        double panX = panningComponent.panXProperty().get();
        double panY = panningComponent.panYProperty().get();
        double newX = xAtPress + deltaXInScreen - panX + panXAtPress;
        double newY = yAtPress + deltaYInScreen - panY + panYAtPress;
        if (snapToGrid.get()) {
            return new Point2D(snapToGrid(newX) - xAtPress, snapToGrid(newY) - yAtPress);
        } else {
            return new Point2D(newX - xAtPress, newY - yAtPress);
        }
    }

    private boolean isDraggedFarEnoughToOvercomeInertia(double screenX, double screenY) {
        return Math.abs(screenX - lastScreenX) > DRAG_INERTIA || Math.abs(screenY - lastScreenY) > DRAG_INERTIA;
    }

    private void panIfOutsideAxisBounds(MouseEvent event) {
        Point2D rootOriginInScreen = panningComponent.getRoot().localToScreen(0, 0);
        double mouseXRelativeToComponent = event.getScreenX() - rootOriginInScreen.getX();
        double mouseYRelativeToComponent = event.getScreenY() - rootOriginInScreen.getY();

        if (mouseXRelativeToComponent <= 0) {
            if (!isRunning(timelineX)) {
                startPanAnimation(-getAnimationSpeed(), timelineX, animatedPanX, panningComponent.panXProperty());
            }
        } else if (mouseXRelativeToComponent >= panningComponent.getRoot().getWidth()) {
            if (!isRunning(timelineX)) {
                startPanAnimation(getAnimationSpeed(), timelineX, animatedPanX, panningComponent.panXProperty());
            }
        } else {
            timelineX.stop();
        }

        if (mouseYRelativeToComponent <= 0) {
            if (!isRunning(timelineY)) {
                startPanAnimation(-getAnimationSpeed(), timelineY, animatedPanY, panningComponent.panYProperty());
            }
        } else if (mouseYRelativeToComponent >= panningComponent.getRoot().getHeight()) {
            if (!isRunning(timelineY)) {
                startPanAnimation(getAnimationSpeed(), timelineY, animatedPanY, panningComponent.panYProperty());
            }
        } else {
            timelineY.stop();
        }
    }

    private void startPanAnimation(double velocity, Timeline timeline, DoubleProperty animatedPan, DoubleProperty pan) {
        if (isRunning(timeline)) {
            return;
        }
        double durationInMillis = 1e10;
        double distance = velocity * durationInMillis;
        animatedPan.set(pan.get());
        KeyValue keyValue = new KeyValue(animatedPan, animatedPan.get() - distance);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(durationInMillis), keyValue);
        timeline.getKeyFrames().setAll(keyFrame);
        timeline.playFromStart();
    }

    private void onAnimatedPanChanged(DoubleProperty animatedPan, DoubleProperty pan) {
        pan.set(Math.round(animatedPan.get()));
        if (onDragged != null) {
            onDragged.accept(calculateDragDelta());
        }
    }

    private double getAnimationSpeed() {
        return (panningComponent.getRoot().getWidth()) / 1000;
    }

    private boolean isRunning(Timeline timeline) {
        return timeline.getStatus() == Animation.Status.RUNNING;
    }

    private double snapToGrid(double value) {
        return Math.round(value / GRID_SIZE) * GRID_SIZE;
    }
}
