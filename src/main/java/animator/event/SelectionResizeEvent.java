package animator.event;

import javafx.geometry.Rectangle2D;
import animator.model.NodeModel;

import java.util.Map;

public class SelectionResizeEvent {

    private final Type type;
    private final Map<NodeModel, Rectangle2D> newPositions;

    public SelectionResizeEvent(Type type, Map<NodeModel, Rectangle2D> newPositions) {
        this.type = type;
        this.newPositions = newPositions;
    }

    public Type getType() {
        return type;
    }

    public Map<NodeModel, Rectangle2D> getNewPositions() {
        return newPositions;
    }

    public enum Type {
        START, DRAG, FINISHED
    }
}
