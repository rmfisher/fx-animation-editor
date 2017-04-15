package animator.command;

import animator.event.TimelineSceneSynchronizer;
import animator.model.*;
import animator.util.ModelFunctions;

import java.util.HashMap;
import java.util.Map;

public abstract class AddNodeCommand implements Command {

    private final Map<KeyFrameModel, Map<AnimatableField, KeyValueModel>> keyValues = new HashMap<>();
    private final NodeModel nodeModel;
    private final TimelineModel timelineModel;
    private final TimelineSceneSynchronizer timelineSceneSynchronizer;

    public AddNodeCommand(NodeModel nodeModel, TimelineModel timelineModel, TimelineSceneSynchronizer timelineSceneSynchronizer) {
        this.nodeModel = nodeModel;
        this.timelineModel = timelineModel;
        this.timelineSceneSynchronizer = timelineSceneSynchronizer;
        createKeyValuesToAdd();
    }

    public Map<KeyFrameModel, Map<AnimatableField, KeyValueModel>> getKeyValues() {
        return keyValues;
    }

    protected void addKeyValues() {
        timelineModel.getKeyFrames().forEach(keyFrame -> keyFrame.getKeyValues().put(nodeModel, keyValues.get(keyFrame)));
        timelineSceneSynchronizer.onNodeAdded(nodeModel);
    }

    protected void removeKeyValues() {
        timelineSceneSynchronizer.onNodeRemoved(nodeModel);
        timelineModel.getKeyFrames().forEach(keyFrame -> keyFrame.getKeyValues().remove(nodeModel, keyValues.get(keyFrame)));
    }

    private void createKeyValuesToAdd() {
        timelineModel.getKeyFrames().forEach(this::createKeyValuesToAdd);
    }

    private void createKeyValuesToAdd(KeyFrameModel keyFrame) {
        Map<AnimatableField, KeyValueModel> keyValues = ModelFunctions.createKeyValues(nodeModel);
        if (timelineModel.getKeyFrames().indexOf(keyFrame) > 0) {
            keyValues.values().forEach(v -> v.setValue(null));
        }
        this.keyValues.put(keyFrame, keyValues);
    }
}
