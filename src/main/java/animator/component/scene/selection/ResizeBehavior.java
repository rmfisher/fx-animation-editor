package animator.component.scene.selection;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import animator.component.scene.panning.PanningComponent;
import animator.component.util.widget.EightBoxes;

import java.util.function.Consumer;

public class ResizeBehavior {

    private static final double GRID_SIZE = 10;

    private final BooleanProperty snapToGrid = new SimpleBooleanProperty();

    private Runnable onResizeStarted;
    private Consumer<Rectangle2D> onResized;
    private Runnable onResizeDone;
    private ResizerComponent resizerComponent;
    private EightBoxes resizers;
    private PanningComponent panningComponent;

    private double xAtPress;
    private double yAtPress;
    private double widthAtPress;
    private double heightAtPress;
    private double screenXAtPress;
    private double screenYAtPress;
    private double lastScreenX;
    private double lastScreenY;
    private double panXAtPress;
    private double panYAtPress;
    private boolean dragActive;
    private double minWidth;
    private double minHeight;

    public void apply(ResizerComponent resizerComponent, PanningComponent panningComponent) {
        this.resizerComponent = resizerComponent;
        this.resizers = resizerComponent.getResizers();
        this.panningComponent = panningComponent;

        addEventHandlers(resizers.getTopLeft());
        addEventHandlers(resizers.getTop());
        addEventHandlers(resizers.getTopRight());
        addEventHandlers(resizers.getRight());
        addEventHandlers(resizers.getBottomRight());
        addEventHandlers(resizers.getBottom());
        addEventHandlers(resizers.getBottomLeft());
        addEventHandlers(resizers.getLeft());
    }

    void setOnResizeStarted(Runnable onResizeStarted) {
        this.onResizeStarted = onResizeStarted;
    }

    void setOnResized(Consumer<Rectangle2D> onResized) {
        this.onResized = onResized;
    }

    void setOnResizeDone(Runnable onResizeDone) {
        this.onResizeDone = onResizeDone;
    }

    BooleanProperty snapToGridProperty() {
        return snapToGrid;
    }

    void setMinSize(double minWidth, double minHeight) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
    }

    private void addEventHandlers(Node resizer) {
        resizer.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onPressed);
        resizer.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onDragged);
        resizer.addEventHandler(MouseEvent.MOUSE_RELEASED, this::onReleased);
    }

    private void onPressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            xAtPress = resizerComponent.xProperty().get();
            yAtPress = resizerComponent.yProperty().get();
            widthAtPress = resizerComponent.widthProperty().get();
            heightAtPress = resizerComponent.heightProperty().get();

            screenXAtPress = event.getScreenX();
            screenYAtPress = event.getScreenY();
            lastScreenX = screenXAtPress;
            lastScreenY = screenYAtPress;
            panXAtPress = panningComponent.panXProperty().get();
            panYAtPress = panningComponent.panYProperty().get();
            dragActive = true;
            if (onResizeStarted != null) {
                onResizeStarted.run();
            }
            event.consume();
        }
    }

    private void onDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            if (dragActive) {
                lastScreenX = event.getScreenX();
                lastScreenY = event.getScreenY();
                if (onResized != null && event.getSource() instanceof Region) {
                    Rectangle2D result = calculateNewSize((Region) event.getSource());
                    if (result != null) {
                        onResized.accept(result);
                    }
                }
            }
            event.consume();
        }
    }

    private void onReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            dragActive = false;
            if (onResizeDone != null) {
                onResizeDone.run();
            }
            event.consume();
        }
    }

    private Rectangle2D calculateNewSize(Region resizer) {
        double deltaXInScreen = lastScreenX - screenXAtPress;
        double deltaYInScreen = lastScreenY - screenYAtPress;
        double panningDeltaX = panningComponent.panXProperty().get() - panXAtPress;
        double panningDeltaY = panningComponent.panYProperty().get() - panYAtPress;

        double x = xAtPress;
        double y = yAtPress;
        double width = widthAtPress;
        double height = heightAtPress;

        if (isAnyRight(resizer)) {
            double proposedWidth = widthAtPress + deltaXInScreen - panningDeltaX;
            if (snapToGrid.get()) {
                double snappedRightEdge = snapToGrid(x + proposedWidth);
                proposedWidth = snappedRightEdge - x;
            }
            width = Math.max(proposedWidth, minWidth);
        }
        if (isAnyBottom(resizer)) {
            double proposedHeight = heightAtPress + deltaYInScreen - panningDeltaY;
            if (snapToGrid.get()) {
                double snappedBottomEdge = snapToGrid(y + proposedHeight);
                proposedHeight = snappedBottomEdge - y;
            }
            height = Math.max(proposedHeight, minHeight);
        }
        if (isAnyLeft(resizer)) {
            double proposedX = xAtPress + deltaXInScreen - panningDeltaX;
            if (snapToGrid.get()) {
                proposedX = snapToGrid(proposedX);
            }
            width = Math.max(xAtPress + widthAtPress - proposedX, minWidth);
            x = xAtPress + widthAtPress - width;
        }
        if (isAnyTop(resizer)) {
            double proposedY = yAtPress + deltaYInScreen - panningDeltaY;
            if (snapToGrid.get()) {
                proposedY = snapToGrid(proposedY);
            }
            height = Math.max(yAtPress + heightAtPress - proposedY, minHeight);
            y = yAtPress + heightAtPress - height;
        }

        return new Rectangle2D(x, y, width, height);
    }

    private boolean isAnyRight(Region r) {
        return r.equals(resizers.getRight()) || r.equals(resizers.getBottomRight()) || r.equals(resizers.getTopRight());
    }

    private boolean isAnyBottom(Region r) {
        return r.equals(resizers.getBottom()) || r.equals(resizers.getBottomLeft()) || r.equals(resizers.getBottomRight());
    }

    private boolean isAnyLeft(Region r) {
        return r.equals(resizers.getLeft()) || r.equals(resizers.getTopLeft()) || r.equals(resizers.getBottomLeft());
    }

    private boolean isAnyTop(Region r) {
        return r.equals(resizers.getTop()) || r.equals(resizers.getTopLeft()) || r.equals(resizers.getTopRight());
    }

    private static double snapToGrid(double value) {
        return Math.round(value / GRID_SIZE) * GRID_SIZE;
    }
}
