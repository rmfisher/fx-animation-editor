package animator.persist.model;

import java.util.List;

public class PKeyFrameModel {

    private List<PNodeStateModel> nodeStates;
    private double time;

    public List<PNodeStateModel> getNodeStates() {
        return nodeStates;
    }

    public void setNodeStates(List<PNodeStateModel> nodeStates) {
        this.nodeStates = nodeStates;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
