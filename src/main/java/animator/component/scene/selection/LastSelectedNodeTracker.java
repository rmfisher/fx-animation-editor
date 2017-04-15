package animator.component.scene.selection;

import animator.model.NodeModel;
import animator.model.SceneModel;

import javax.inject.Inject;

public class LastSelectedNodeTracker extends SelectionListener {

    private final SceneModel sceneModel;

    @Inject
    public LastSelectedNodeTracker(SceneModel sceneModel) {
        super(sceneModel);
        this.sceneModel = sceneModel;
    }

    @Override
    protected void onSelectionChanged(NodeModel nodeModel) {
        if (nodeModel.isSelected()) {
            sceneModel.setLastSelectedNode(nodeModel);
        }
        if (!nodeModel.isSelected()) {
            sceneModel.setLastSelectedNode(sceneModel.getSelectedNodes().findFirst().orElseGet(() -> null));
        }
    }
}
