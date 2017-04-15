package animator.persist.model;

import animator.persist.model.interpolator.PInterpolatorListModel;

public class PAnimationModel {

    private PTimelineModel timeline;
    private PSceneModel scene;
    private PInterpolatorListModel interpolatorList;

    public PTimelineModel getTimeline() {
        return timeline;
    }

    public void setTimeline(PTimelineModel timeline) {
        this.timeline = timeline;
    }

    public PSceneModel getScene() {
        return scene;
    }

    public void setScene(PSceneModel scene) {
        this.scene = scene;
    }

    public PInterpolatorListModel getInterpolatorList() {
        return interpolatorList;
    }

    public void setInterpolatorList(PInterpolatorListModel interpolatorList) {
        this.interpolatorList = interpolatorList;
    }
}
