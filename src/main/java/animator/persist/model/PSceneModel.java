package animator.persist.model;

import java.util.List;

public class PSceneModel {

    private List<PNodeModel> nodes;
    private boolean snapToGrid;

    public List<PNodeModel> getNodes() {
        return nodes;
    }

    public void setNodes(List<PNodeModel> nodes) {
        this.nodes = nodes;
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }
}
