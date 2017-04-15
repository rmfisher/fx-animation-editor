package animator.command;

import animator.model.KeyFrameModel;
import animator.model.NodeModel;
import animator.model.TimelineModel;

import java.util.Map;

public class KeyValueChangeCommand implements Command {

    private final Map<NodeModel, KeyValueChange> keyValueChanges;
    private final TimelineModel timelineModel;
    private final KeyFrameModel keyFrame;

    public KeyValueChangeCommand(Map<NodeModel, KeyValueChange> keyValueChanges, TimelineModel timelineModel) {
        this.keyValueChanges = keyValueChanges;
        this.timelineModel = timelineModel;
        this.keyFrame = timelineModel.getSelectedKeyFrame();
    }

    public Map<NodeModel, KeyValueChange> getKeyValueChanges() {
        return keyValueChanges;
    }

    @Override
    public void execute() {
        timelineModel.setSelectedKeyFrame(keyFrame);
        keyValueChanges.values().forEach(change -> change.getKeyValue().setValue(change.getNewValue()));
        keyValueChanges.keySet().forEach(n -> n.setSelected(true));
    }

    @Override
    public void undo() {
        timelineModel.setSelectedKeyFrame(keyFrame);
        keyValueChanges.values().forEach(change -> change.getKeyValue().setValue(change.getOldValue()));
        keyValueChanges.keySet().forEach(n -> n.setSelected(true));
    }
}
