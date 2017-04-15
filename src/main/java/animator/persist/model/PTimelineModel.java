package animator.persist.model;

import java.util.List;

public class PTimelineModel {

    private List<PKeyFrameModel> keyFrames;

    public List<PKeyFrameModel> getKeyFrames() {
        return keyFrames;
    }

    public void setKeyFrames(List<PKeyFrameModel> keyFrames) {
        this.keyFrames = keyFrames;
    }
}
