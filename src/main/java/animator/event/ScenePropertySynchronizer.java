package animator.event;

import javafx.beans.value.ChangeListener;
import animator.util.RateLimitedChangeListener;
import animator.model.*;
import animator.model.interpolator.InterpolatorModel;
import animator.util.ModelFunctions;

import javax.inject.Inject;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class ScenePropertySynchronizer {

    private final TimelineModel timelineModel;
    private final PropertyModel propertyModel;
    private final SceneModel sceneModel;
    private final Map<AnimatableField, ChangeListener<Object>> valueChangeListeners;
    private final Map<AnimatableField, ChangeListener<InterpolatorModel>> interpolatorChangeListeners;

    @Inject
    public ScenePropertySynchronizer(TimelineModel timelineModel, PropertyModel propertyModel, SceneModel sceneModel) {
        this.timelineModel = timelineModel;
        this.propertyModel = propertyModel;
        this.sceneModel = sceneModel;
        this.valueChangeListeners = createValueChangeListeners();
        this.interpolatorChangeListeners = createInterpolatorChangeListeners();
        sceneModel.lastSelectedNodeProperty().addListener((v, o, n) -> onLastSelectedNodeChanged(o, n));
        timelineModel.selectedKeyFrameProperty().addListener((v, o, n) -> onKeyFrameChanged(o, n));
    }

    private Map<AnimatableField, ChangeListener<Object>> createValueChangeListeners() {
        return stream(AnimatableField.values()).collect(Collectors.toMap(Function.identity(), this::createValueChangeListener));
    }

    private Map<AnimatableField, ChangeListener<InterpolatorModel>> createInterpolatorChangeListeners() {
        return stream(AnimatableField.values()).collect(Collectors.toMap(Function.identity(), this::createInterpolatorChangeListener));
    }

    private ChangeListener<Object> createValueChangeListener(AnimatableField field) {
        return new RateLimitedChangeListener<>((v, o, n) -> updateProperty(field, n));
    }

    private ChangeListener<InterpolatorModel> createInterpolatorChangeListener(AnimatableField field) {
        return (v, o, n) -> updateInterpolator(field, n);
    }

    private void onLastSelectedNodeChanged(NodeModel oldValue, NodeModel newValue) {
        if (timelineModel.getSelectedKeyFrame() != null) {
            if (oldValue != null) {
                removeListeners(timelineModel.getSelectedKeyFrame().getKeyValues().get(oldValue));
            }
            if (newValue != null) {
                addListenersAndSync(timelineModel.getSelectedKeyFrame().getKeyValues().get(newValue));
            }
        }
    }

    private void onKeyFrameChanged(KeyFrameModel oldValue, KeyFrameModel newValue) {
        if (sceneModel.getLastSelectedNode() != null) {
            if (oldValue != null) {
                removeListeners(oldValue.getKeyValues().get(sceneModel.getLastSelectedNode()));
            }
            if (newValue != null) {
                addListenersAndSync(newValue.getKeyValues().get(sceneModel.getLastSelectedNode()));
            }
        }
    }

    private void removeListeners(Map<AnimatableField, KeyValueModel> keyValues) {
        keyValues.values().forEach(v -> v.valueProperty().removeListener(valueChangeListeners.get(v.getField())));
        keyValues.values().forEach(v -> v.interpolatorProperty().removeListener(interpolatorChangeListeners.get(v.getField())));
    }

    private void addListenersAndSync(Map<AnimatableField, KeyValueModel> keyValues) {
        ModelFunctions.clear(propertyModel.getProperties());
        keyValues.values().forEach(v -> v.valueProperty().addListener(valueChangeListeners.get(v.getField())));
        keyValues.values().forEach(v -> v.interpolatorProperty().addListener(interpolatorChangeListeners.get(v.getField())));
        keyValues.values().forEach(v -> updateProperty(v.getField(), v.getValue()));
        keyValues.values().forEach(v -> updateInterpolator(v.getField(), v.getInterpolator()));
    }

    private void updateProperty(AnimatableField field, Object newValue) {
        propertyModel.getProperties().get(field).setValue(newValue);
    }

    private void updateInterpolator(AnimatableField field, InterpolatorModel newInterpolator) {
        propertyModel.getProperties().get(field).setInterpolator(newInterpolator);
    }
}
