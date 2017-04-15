package animator.event;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import animator.model.KeyFrameModel;
import animator.model.PlayerModel;
import animator.model.TimelineModel;

import javax.inject.Inject;

public class TimelineDurationTracker {

    private final ChangeListener<Number> listener = (v, o, n) -> calculateTimelineDuration();
    private final TimelineModel timelineModel;
    private final PlayerModel playerModel;

    @Inject
    public TimelineDurationTracker(TimelineModel timelineModel, PlayerModel playerModel) {
        this.timelineModel = timelineModel;
        this.playerModel = playerModel;
        addListeners();
    }

    private void addListeners() {
        timelineModel.keyFramesProperty().addListener(this::onKeyFramesChanged);
    }

    private void onKeyFramesChanged(ListChangeListener.Change<? extends KeyFrameModel> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(k -> k.timeProperty().addListener(listener));
            }
            if (change.wasRemoved()) {
                change.getRemoved().forEach(k -> k.timeProperty().removeListener(listener));
            }
        }
        calculateTimelineDuration();
    }

    private void calculateTimelineDuration() {
        timelineModel.getKeyFrames().stream().map(KeyFrameModel::getTime).reduce((a, b) -> a + b).ifPresent(playerModel::setFinalTime);
    }
}
