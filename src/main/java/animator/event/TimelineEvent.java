package animator.event;

import animator.model.KeyFrameModel;

public class TimelineEvent {

    private final Type type;
    private final KeyFrameModel keyFrame;

    public TimelineEvent(Type type, KeyFrameModel keyFrame) {
        this.type = type;
        this.keyFrame = keyFrame;
    }

    public Type getType() {
        return type;
    }

    public KeyFrameModel getKeyFrame() {
        return keyFrame;
    }

    public enum Type {
        ADD, DELETE
    }
}
