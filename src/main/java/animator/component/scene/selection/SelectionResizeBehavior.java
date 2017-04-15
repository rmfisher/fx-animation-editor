package animator.component.scene.selection;

import com.google.common.eventbus.EventBus;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import animator.component.scene.panning.PanningComponent;
import animator.event.SelectionResizeEvent;
import animator.model.NodeModel;
import animator.model.RectangleModel;
import animator.model.SceneModel;

import javax.inject.Inject;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectionResizeBehavior {

    private final ResizeBehavior resizeBehavior = new ResizeBehavior();
    private final SceneModel model;
    private final PanningComponent panningComponent;
    private final ResizerComponent resizer;
    private final EventBus eventBus;

    private Map<NodeModel, Rectangle2D> startPositions;
    private Map<NodeModel, Rectangle2D> currentPositions;
    private Rectangle2D resizerPositionAtStart;
    private Rectangle2D currentResizerPosition;
    private double scaleX;
    private double scaleY;

    @Inject
    public SelectionResizeBehavior(SceneModel model, PanningComponent panningComponent, ResizerComponent resizer, EventBus eventBus) {
        this.model = model;
        this.panningComponent = panningComponent;
        this.resizer = resizer;
        this.eventBus = eventBus;
        addHandlers();
    }

    private void addHandlers() {
        resizeBehavior.snapToGridProperty().bind(model.snapToGridProperty());
        resizeBehavior.apply(resizer, panningComponent);
        resizeBehavior.setOnResizeStarted(this::onResizeStarted);
        resizeBehavior.setOnResized(this::onResized);
        resizeBehavior.setOnResizeDone(this::onResizeDone);
    }

    private void onResizeStarted() {
        startPositions = selectedRectangles().collect(Collectors.toMap(Function.identity(), this::getCurrentPosition));
        resizerPositionAtStart = new Rectangle2D(resizer.getX(), resizer.getY(), resizer.getWidth(), resizer.getHeight());
        eventBus.post(new SelectionResizeEvent(SelectionResizeEvent.Type.START, null));
        setMinSize();
    }

    private Rectangle2D getCurrentPosition(RectangleModel rectangleModel) {
        Rectangle rectangle = rectangleModel.getNode();
        return new Rectangle2D(rectangle.getLayoutX(), rectangle.getLayoutY(), rectangle.getWidth(), rectangle.getHeight());
    }

    private void onResized(Rectangle2D newSize) {
        currentResizerPosition = newSize;
        scaleX = currentResizerPosition.getWidth() / resizerPositionAtStart.getWidth();
        scaleY = currentResizerPosition.getHeight() / resizerPositionAtStart.getHeight();
        currentPositions = selectedRectangles().collect(Collectors.toMap(Function.identity(), this::getNewPosition));
        eventBus.post(new SelectionResizeEvent(SelectionResizeEvent.Type.DRAG, currentPositions));
    }

    private Rectangle2D getNewPosition(RectangleModel node) {
        Rectangle2D startPosition = startPositions.get(node);
        double transformedMinX = transformX(startPosition.getMinX());
        double transformedMinY = transformY(startPosition.getMinY());
        double transformedMaxX = transformX(startPosition.getMaxX());
        double transformedMaxY = transformY(startPosition.getMaxY());
        return new Rectangle2D(transformedMinX, transformedMinY, transformedMaxX - transformedMinX, transformedMaxY - transformedMinY);
    }

    private double transformX(double value) {
        return currentResizerPosition.getMinX() + Math.round(scaleX * (value - resizerPositionAtStart.getMinX()));
    }

    private double transformY(double value) {
        return currentResizerPosition.getMinY() + Math.round(scaleY * (value - resizerPositionAtStart.getMinY()));
    }

    private void onResizeDone() {
        if (startPositions != null && currentPositions != null && startPositions.size() == currentPositions.size()) {
            eventBus.post(new SelectionResizeEvent(SelectionResizeEvent.Type.FINISHED, currentPositions));
        }
        startPositions = null;
        currentPositions = null;
    }

    private void setMinSize() {
        double minWidth = 0;
        double minHeight = 0;
        double selectionWidth = resizerPositionAtStart.getWidth();
        double selectionHeight = resizerPositionAtStart.getHeight();
        for (RectangleModel rectangleModel : selectedRectangles().collect(Collectors.toList())) {
            double width = rectangleModel.getNode().getWidth();
            double height = rectangleModel.getNode().getHeight();
            double minWidthCandidate = Math.floor(selectionWidth * 5 / width);
            double minHeightCandidate = Math.floor(selectionHeight * 5 / height);
            if (minWidthCandidate > minWidth) {
                minWidth = minWidthCandidate;
            }
            if (minHeightCandidate > minHeight) {
                minHeight = minHeightCandidate;
            }
        }
        resizeBehavior.setMinSize(minWidth, minHeight);
    }

    private Stream<RectangleModel> selectedRectangles() {
        return model.getSelectedNodes().filter(RectangleModel.class::isInstance).map(RectangleModel.class::cast);
    }
}
