package animator.component.scene.selection;

import com.google.common.eventbus.EventBus;
import javafx.geometry.Point2D;
import animator.component.scene.panning.PanningComponent;
import animator.event.SelectionDragEvent;
import animator.model.NodeModel;
import animator.model.SceneModel;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SelectionDragBehavior extends NodeChangeListener {

    private final Map<NodeModel, DragBehavior> dragBehaviors = new HashMap<>();
    private final SceneModel sceneModel;
    private final PanningComponent panningComponent;
    private final EventBus eventBus;

    private Map<NodeModel, Point2D> startPositions = new HashMap<>();
    private Map<NodeModel, Point2D> currentPositions = new HashMap<>();

    @Inject
    public SelectionDragBehavior(SceneModel sceneModel, PanningComponent panningComponent, EventBus eventBus) {
        super(sceneModel);
        this.sceneModel = sceneModel;
        this.panningComponent = panningComponent;
        this.eventBus = eventBus;
    }

    @Override
    protected void onNodeAdded(NodeModel nodeModel) {
        DragBehavior dragBehavior = new DragBehavior();
        dragBehavior.apply(nodeModel.getNode(), panningComponent);
        dragBehavior.snapToGridProperty().bind(sceneModel.snapToGridProperty());
        dragBehavior.setOnDragStarted(this::onDragStarted);
        dragBehavior.setOnDragged(this::onDragged);
        dragBehavior.setOnDragDone(this::onDragDone);
        dragBehaviors.put(nodeModel, dragBehavior);
    }

    @Override
    protected void onNodeRemoved(NodeModel nodeModel) {
        DragBehavior dragBehavior = dragBehaviors.get(nodeModel);
        dragBehavior.dispose(nodeModel.getNode());
        dragBehavior.snapToGridProperty().unbind();
        dragBehavior.setOnDragStarted(null);
        dragBehavior.setOnDragged(null);
        dragBehavior.setOnDragDone(null);
        dragBehaviors.remove(nodeModel);
    }

    private void onDragStarted() {
        startPositions = sceneModel.getSelectedNodes().collect(Collectors.toMap(Function.identity(), this::getCurrentPosition));
        eventBus.post(new SelectionDragEvent(SelectionDragEvent.Type.START, null));
    }

    private Point2D getCurrentPosition(NodeModel nodeModel) {
        return new Point2D(nodeModel.getNode().getLayoutX(), nodeModel.getNode().getLayoutY());
    }

    private void onDragged(Point2D dragDelta) {
        currentPositions = moveStartPositionsBy(dragDelta);
        eventBus.post(new SelectionDragEvent(SelectionDragEvent.Type.DRAG, currentPositions));
    }

    private Map<NodeModel, Point2D> moveStartPositionsBy(Point2D dragDelta) {
        return startPositions.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().add(dragDelta)));
    }

    private void onDragDone() {
        if (currentPositions != null && startPositions != null && currentPositions.size() == startPositions.size()) {
            eventBus.post(new SelectionDragEvent(SelectionDragEvent.Type.FINISHED, currentPositions));
        }
        startPositions = null;
        currentPositions = null;
    }
}
