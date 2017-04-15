package animator.persist.model;

import java.util.List;

public class PNodeStateModel {

    private int nodeId;
    private List<PDoubleKeyValueModel> doubleKeyValues;
    private List<PColorKeyValueModel> colorKeyValues;

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public List<PDoubleKeyValueModel> getDoubleKeyValues() {
        return doubleKeyValues;
    }

    public void setDoubleKeyValues(List<PDoubleKeyValueModel> doubleKeyValues) {
        this.doubleKeyValues = doubleKeyValues;
    }

    public List<PColorKeyValueModel> getColorKeyValues() {
        return colorKeyValues;
    }

    public void setColorKeyValues(List<PColorKeyValueModel> colorKeyValues) {
        this.colorKeyValues = colorKeyValues;
    }
}
