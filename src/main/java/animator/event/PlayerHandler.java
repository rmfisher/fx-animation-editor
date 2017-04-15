package animator.event;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import animator.model.*;
import animator.util.ModelFunctions;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerHandler {

    private final Timeline timeline = new Timeline();
    private final TimelineModel timelineModel;
    private final SceneModel sceneModel;
    private final PlayerModel playerModel;
    private final Set<NodeModel> selectionOnPlayStart = new HashSet<>();
    private KeyFrameModel selectedKeyFrameOnPlayStart;

    @Inject
    public PlayerHandler(TimelineModel timelineModel, SceneModel sceneModel, PlayerModel playerModel) {
        this.timelineModel = timelineModel;
        this.sceneModel = sceneModel;
        this.playerModel = playerModel;
        initBindings();
    }

    private void initBindings() {
        playerModel.animationPossibleProperty().bind(timelineModel.keyFramesProperty().sizeProperty().greaterThan(1));
        playerModel.playModeProperty().addListener((v, o, n) -> onPlayModeChanged(n));
        playerModel.runningProperty().addListener((v, o, n) -> onRunningChanged(n));
    }

    private void onRunningChanged(boolean running) {
        if (running) {
            timeline.play();
        } else {
            timeline.pause();
        }
    }

    private void onPlayModeChanged(boolean playMode) {
        if (!playMode) {
            selectionOnPlayStart.forEach(n -> n.setSelected(true));
            selectionOnPlayStart.clear();
            timeline.stop();
            timeline.getKeyFrames().clear();
            playerModel.currentTimeProperty().unbind();
            timelineModel.setSelectedKeyFrame(selectedKeyFrameOnPlayStart);
        } else {
            selectionOnPlayStart.clear();
            selectionOnPlayStart.addAll(sceneModel.getSelectedNodes().collect(Collectors.toSet()));
            sceneModel.getNodes().forEach(n -> n.setSelected(false));
            selectedKeyFrameOnPlayStart = timelineModel.getSelectedKeyFrame();
            timelineModel.setSelectedKeyFrame(null);
            playerModel.currentTimeProperty().bind(currentTimeInSeconds());
            timeline.getKeyFrames().addAll(timelineModel.getKeyFrames().stream().map(this::createKeyFrame).collect(Collectors.toList()));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.playFromStart();
        }
    }

    private KeyFrame createKeyFrame(KeyFrameModel keyFrameModel) {
        return new KeyFrame(Duration.seconds(keyFrameModel.getAbsoluteTime()), createKeyValues(keyFrameModel));
    }

    private KeyValue[] createKeyValues(KeyFrameModel keyFrameModel) {
        List<KeyValue> keyValues = new ArrayList<>();
        keyFrameModel.getKeyValues().forEach((n, m) -> m.values().stream().filter(v -> v.getValue() != null).forEach(v -> keyValues.add(createKeyValue(n, v))));
        return keyValues.toArray(new KeyValue[]{});
    }

    private KeyValue createKeyValue(NodeModel nodeModel, KeyValueModel keyValueModel) {
        Optional<DoubleProperty> doubleProperty = ModelFunctions.getDoubleProperty(nodeModel.getNode(), keyValueModel.getField());
        if (doubleProperty.isPresent()) {
            return new KeyValue(doubleProperty.get(), (double) keyValueModel.getValue(), keyValueModel.getInterpolator().toFxInterpolator());
        }
        Optional<ObjectProperty<Paint>> paintProperty = ModelFunctions.getPaintProperty(nodeModel.getNode(), keyValueModel.getField());
        if (paintProperty.isPresent()) {
            return new KeyValue(paintProperty.get(), (Paint) keyValueModel.getValue(), keyValueModel.getInterpolator().toFxInterpolator());
        }
        throw new UnsupportedOperationException();
    }

    private DoubleBinding currentTimeInSeconds() {
        return Bindings.createDoubleBinding(() -> timeline.getCurrentTime().toSeconds(), timeline.currentTimeProperty());
    }
}
