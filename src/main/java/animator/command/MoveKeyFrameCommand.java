package animator.command;

import animator.model.KeyFrameModel;
import animator.model.TimelineModel;

public class MoveKeyFrameCommand implements Command {

    private final TimelineModel timelineModel;
    private final KeyFrameModel keyFrameModel;
    private final int oldIndex;
    private final int newIndex;

    public MoveKeyFrameCommand(TimelineModel timelineModel, int oldIndex, int newIndex) {
        this.timelineModel = timelineModel;
        this.keyFrameModel = timelineModel.getKeyFrames().get(oldIndex);
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }

    @Override
    public void execute() {
        timelineModel.setSelectedKeyFrame(keyFrameModel);
        timelineModel.getKeyFrames().remove(keyFrameModel);
        timelineModel.getKeyFrames().add(newIndex, keyFrameModel);
    }

    @Override
    public void undo() {
        timelineModel.setSelectedKeyFrame(keyFrameModel);
        timelineModel.getKeyFrames().remove(keyFrameModel);
        timelineModel.getKeyFrames().add(oldIndex, keyFrameModel);
    }
}
