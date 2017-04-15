package animator.command;

import animator.model.KeyFrameModel;
import animator.model.TimelineModel;

public class DeleteKeyFrameCommand implements Command {

    private final KeyFrameModel keyFrame;
    private final TimelineModel timelineModel;
    private final int index;

    public DeleteKeyFrameCommand(KeyFrameModel keyFrame, TimelineModel timelineModel) {
        this.keyFrame = keyFrame;
        this.timelineModel = timelineModel;
        index = timelineModel.getKeyFrames().indexOf(keyFrame);
    }

    @Override
    public void execute() {
        if (keyFrame.equals(timelineModel.getSelectedKeyFrame())) {
            timelineModel.setSelectedKeyFrame(timelineModel.getKeyFrames().get(index - 1));
        }
        timelineModel.getKeyFrames().remove(keyFrame);
    }

    @Override
    public void undo() {
        timelineModel.getKeyFrames().add(index, keyFrame);
        timelineModel.setSelectedKeyFrame(keyFrame);
    }
}