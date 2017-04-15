package animator.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.stream.Stream;

public class SceneModel {

    private final ListProperty<NodeModel> nodes = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<NodeModel> lastSelectedNode = new SimpleObjectProperty<>();
    private final BooleanProperty snapToGrid = new SimpleBooleanProperty(true);

    public ObservableList<NodeModel> getNodes() {
        return nodes.get();
    }

    public ReadOnlyListProperty<NodeModel> nodesProperty() {
        return nodes;
    }

    public NodeModel getLastSelectedNode() {
        return lastSelectedNode.get();
    }

    public ObjectProperty<NodeModel> lastSelectedNodeProperty() {
        return lastSelectedNode;
    }

    public void setLastSelectedNode(NodeModel lastSelectedNode) {
        this.lastSelectedNode.set(lastSelectedNode);
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

    public Stream<NodeModel> getSelectedNodes() {
        return nodes.stream().filter(NodeModel::isSelected);
    }
}
