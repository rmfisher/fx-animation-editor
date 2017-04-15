package animator.command;

import animator.model.KeyFrameModel;

public class TimeChangeCommand implements Command {

    private final KeyFrameModel keyFrame;
    private final double oldTime;
    private final double newTime;

    public TimeChangeCommand(KeyFrameModel keyFrame, double oldTime, double newTime) {
        this.keyFrame = keyFrame;
        this.oldTime = oldTime;
        this.newTime = newTime;
    }

    @Override
    public void execute() {
        keyFrame.setTime(newTime);
    }

    @Override
    public void undo() {
        keyFrame.setTime(oldTime);
    }
}
