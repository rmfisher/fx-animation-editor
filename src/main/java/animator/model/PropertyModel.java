package animator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import animator.util.ModelFunctions;

import java.util.HashMap;
import java.util.Map;

public class PropertyModel {

    private final Map<AnimatableField, KeyValueModel> properties = new HashMap<>();
    private final ObjectProperty<NodeType> nodeType = new SimpleObjectProperty<>();
    private final BooleanProperty nonZeroKeyFrame = new SimpleBooleanProperty();
    private final BooleanProperty snapToGrid = new SimpleBooleanProperty();

    public PropertyModel() {
        properties.putAll(ModelFunctions.createNodeDoubles());
        properties.putAll(ModelFunctions.createRectangleDoubles());
        properties.putAll(ModelFunctions.createPaints());
    }

    public Map<AnimatableField, KeyValueModel> getProperties() {
        return properties;
    }

    public NodeType getNodeType() {
        return nodeType.get();
    }

    public ObjectProperty<NodeType> nodeTypeProperty() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType.set(nodeType);
    }

    public boolean isNonZeroKeyFrame() {
        return nonZeroKeyFrame.get();
    }

    public BooleanProperty nonZeroKeyFrameProperty() {
        return nonZeroKeyFrame;
    }

    public void setNonZeroKeyFrame(boolean nonZeroKeyFrame) {
        this.nonZeroKeyFrame.set(nonZeroKeyFrame);
    }

    public boolean isSnapToGrid() {
        return snapToGrid.get();
    }

    public BooleanProperty snapToGridProperty() {
        return snapToGrid;
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid.set(snapToGrid);
    }
}
