package animator.command;

import animator.model.KeyFrameModel;
import animator.model.NodeModel;
import animator.model.TimelineModel;

import java.util.Map;

public class InterpolatorChangeCommand implements Command {

    private final Map<NodeModel, InterpolatorChange> interpolatorChanges;
    private final TimelineModel timelineModel;
    private final KeyFrameModel keyFrame;

    public InterpolatorChangeCommand(Map<NodeModel, InterpolatorChange> interpolatorChanges, TimelineModel timelineModel) {
        this.interpolatorChanges = interpolatorChanges;
        this.timelineModel = timelineModel;
        this.keyFrame = timelineModel.getSelectedKeyFrame();
    }

    public Map<NodeModel, InterpolatorChange> getInterpolatorChanges() {
        return interpolatorChanges;
    }

    @Override
    public void execute() {
        timelineModel.setSelectedKeyFrame(keyFrame);
        interpolatorChanges.values().forEach(change -> change.getKeyValue().setInterpolator(change.getNewValue()));
    }

    @Override
    public void undo() {
        timelineModel.setSelectedKeyFrame(keyFrame);
        interpolatorChanges.values().forEach(change -> change.getKeyValue().setInterpolator(change.getOldValue()));
    }
}
