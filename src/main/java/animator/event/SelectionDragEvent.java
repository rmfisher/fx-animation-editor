package animator.event;

import javafx.geometry.Point2D;
import animator.model.NodeModel;

import java.util.Map;

public class SelectionDragEvent {

    private final Type type;
    private final Map<NodeModel, Point2D> newPositions;

    public SelectionDragEvent(Type type, Map<NodeModel, Point2D> newPositions) {
        this.type = type;
        this.newPositions = newPositions;
    }

    public Type getType() {
        return type;
    }

    public Map<NodeModel, Point2D> getNewPositions() {
        return newPositions;
    }

    public enum Type {
        START, DRAG, FINISHED
    }
}
