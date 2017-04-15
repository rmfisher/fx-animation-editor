package animator.component.scene.selection;

import javafx.collections.ListChangeListener;
import animator.model.NodeModel;
import animator.model.SceneModel;

public abstract class NodeChangeListener {

    public NodeChangeListener(SceneModel sceneModel) {
        sceneModel.getNodes().addListener(this::onNodesChanged);
        sceneModel.getNodes().forEach(this::onNodeAdded);
    }

    protected abstract void onNodeAdded(NodeModel nodeModel);

    protected abstract void onNodeRemoved(NodeModel nodeModel);

    private void onNodesChanged(ListChangeListener.Change<? extends NodeModel> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(this::onNodeAdded);
            }
            if (change.wasRemoved()) {
                change.getRemoved().forEach(this::onNodeRemoved);
            }
        }
    }
}
