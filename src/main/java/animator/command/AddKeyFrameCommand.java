package animator.command;

import animator.model.KeyFrameModel;
import animator.model.TimelineModel;

public class AddKeyFrameCommand implements Command {

    private final KeyFrameModel keyFrame;
    private final TimelineModel timelineModel;
    private final KeyFrameModel previousSelection;

    public AddKeyFrameCommand(KeyFrameModel keyFrame, TimelineModel timelineModel) {
        this.keyFrame = keyFrame;
        this.timelineModel = timelineModel;
        this.previousSelection = timelineModel.getSelectedKeyFrame();
    }

    @Override
    public void execute() {
        timelineModel.getKeyFrames().add(keyFrame);
        timelineModel.setSelectedKeyFrame(keyFrame);
    }

    @Override
    public void undo() {
        timelineModel.getKeyFrames().remove(keyFrame);
        timelineModel.setSelectedKeyFrame(previousSelection);
    }
}
