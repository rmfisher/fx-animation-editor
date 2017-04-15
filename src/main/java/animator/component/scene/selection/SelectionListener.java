package animator.component.scene.selection;

import javafx.beans.value.ChangeListener;
import animator.model.NodeModel;
import animator.model.SceneModel;

import java.util.HashMap;
import java.util.Map;

public abstract class SelectionListener extends NodeChangeListener {

    private final Map<NodeModel, ChangeListener<Boolean>> listeners = new HashMap<>();

    public SelectionListener(SceneModel sceneModel) {
        super(sceneModel);
    }

    protected abstract void onSelectionChanged(NodeModel nodeModel);

    @Override
    protected void onNodeAdded(NodeModel nodeModel) {
        ChangeListener<Boolean> listener = (v, o, n) -> onSelectionChanged(nodeModel);
        nodeModel.selectedProperty().addListener(listener);
        onSelectionChanged(nodeModel);
        listeners.put(nodeModel, listener);
    }

    @Override
    protected void onNodeRemoved(NodeModel nodeModel) {
        nodeModel.selectedProperty().removeListener(listeners.get(nodeModel));
        listeners.remove(nodeModel);
    }
}
