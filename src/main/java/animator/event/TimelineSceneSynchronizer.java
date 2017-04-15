package animator.event;

import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import animator.model.*;
import animator.util.ModelFunctions;

import java.util.Arrays;

public class TimelineSceneSynchronizer {

    private final TimelineModel timelineModel;
    private final SceneModel sceneModel;

    @Inject
    public TimelineSceneSynchronizer(TimelineModel timelineModel, SceneModel sceneModel) {
        this.timelineModel = timelineModel;
        this.sceneModel = sceneModel;
        timelineModel.selectedKeyFrameProperty().addListener((v, o, n) -> updateBindings());
        timelineModel.getKeyFrames().addListener((ListChangeListener<? super KeyFrameModel>) change -> updateBindings());
    }

    public void onNodeAdded(NodeModel node) {
        timelineModel.getSelectedKeyFrame().getKeyValues().get(node).forEach((field, value) -> bind(node, field, value, timelineModel.getSelectedKeyFrame()));
    }

    public void onNodeRemoved(NodeModel node) {
        unbind(node);
    }

    private void updateBindings() {
        if (timelineModel.getSelectedKeyFrame() != null) {
            bindAll(timelineModel.getSelectedKeyFrame());
        } else {
            ModelFunctions.getAllSceneNodes(sceneModel).forEach(this::unbind);
        }
    }

    private void bindAll(KeyFrameModel keyFrame) {
        keyFrame.getKeyValues().forEach((node, values) -> values.forEach((field, value) -> bind(node, field, value, keyFrame)));
    }

    private void bind(NodeModel node, AnimatableField field, KeyValueModel keyValue, KeyFrameModel keyFrame) {
        ModelFunctions.getDoubleProperty(node.getNode(), field).ifPresent(p -> p.bind(getDouble(field, node, keyValue, keyFrame)));
        ModelFunctions.getPaintProperty(node.getNode(), field).ifPresent(p -> p.bind(getColor(field, node, keyValue, keyFrame)));
    }

    private NumberBinding getDouble(AnimatableField field, NodeModel node, KeyValueModel keyValue, KeyFrameModel keyFrame) {
        DoubleBinding currentValue = ModelFunctions.toDoubleBinding(keyValue.valueProperty());
        KeyFrameModel earlier = getEarlierKeyFrameWithNonNullValue(field, node, keyFrame);
        KeyFrameModel later = getLaterKeyFrameWithNonNullValue(field, node, keyFrame);

        if (earlier != null) {
            DoubleProperty earlierTime = earlier.absoluteTimeProperty();
            double earlierValue = (Double) earlier.getKeyValues().get(node).get(field).getValue();

            if (later != null) {
                DoubleProperty laterTime = later.absoluteTimeProperty();
                double laterValue = (Double) later.getKeyValues().get(node).get(field).getValue();

                DoubleBinding interpolated = Bindings.createDoubleBinding(() -> {
                    double timeFraction = (keyFrame.getAbsoluteTime() - earlierTime.get()) / (laterTime.get() - earlierTime.get());
                    double interpolatorFactor = later.getKeyValues().get(node).get(field).getInterpolator().curve(timeFraction);
                    return (double) Math.round(earlierValue + (laterValue - earlierValue) * interpolatorFactor);
                }, earlierTime, laterTime, keyFrame.absoluteTimeProperty());

                return Bindings.when(keyValue.valueProperty().isNotNull()).then(currentValue).otherwise(interpolated);
            } else {
                return Bindings.when(keyValue.valueProperty().isNotNull()).then(currentValue).otherwise(earlierValue);
            }
        } else {
            return currentValue;
        }
    }

    private ObjectBinding<Paint> getColor(AnimatableField field, NodeModel node, KeyValueModel keyValue, KeyFrameModel keyFrame) {
        ObjectBinding<Paint> currentValue = ModelFunctions.toPaintBinding(keyValue.valueProperty(), Color.TRANSPARENT);
        KeyFrameModel earlier = getEarlierKeyFrameWithNonNullValue(field, node, keyFrame);
        KeyFrameModel later = getLaterKeyFrameWithNonNullValue(field, node, keyFrame);

        if (earlier != null) {
            DoubleProperty earlierTime = earlier.absoluteTimeProperty();
            Color earlierValue = (Color) earlier.getKeyValues().get(node).get(field).getValue();

            if (later != null) {
                DoubleProperty laterTime = later.absoluteTimeProperty();
                Color laterValue = (Color) later.getKeyValues().get(node).get(field).getValue();

                ObjectBinding<Paint> interpolated = Bindings.createObjectBinding(() -> {
                    double timeFraction = (keyFrame.getAbsoluteTime() - earlierTime.get()) / (laterTime.get() - earlierTime.get());
                    double interpolatorFactor = later.getKeyValues().get(node).get(field).getInterpolator().curve(timeFraction);
                    return earlierValue.interpolate(laterValue, interpolatorFactor);
                }, earlierTime, laterTime, keyFrame.absoluteTimeProperty());

                return Bindings.when(keyValue.valueProperty().isNotNull()).then(currentValue).otherwise(interpolated);
            } else {
                return Bindings.when(keyValue.valueProperty().isNotNull()).then(currentValue).otherwise(earlierValue);
            }
        } else {
            return currentValue;
        }
    }

    private KeyFrameModel getEarlierKeyFrameWithNonNullValue(AnimatableField field, NodeModel node, KeyFrameModel keyFrame) {
        int keyFrameIndex = timelineModel.getKeyFrames().indexOf(keyFrame);
        for (int i = keyFrameIndex - 1; i >= 0; i--) {
            Object value = timelineModel.getKeyFrames().get(i).getKeyValues().get(node).get(field).getValue();
            if (value != null) {
                return timelineModel.getKeyFrames().get(i);
            }
        }
        return null;
    }

    private KeyFrameModel getLaterKeyFrameWithNonNullValue(AnimatableField field, NodeModel node, KeyFrameModel keyFrame) {
        int keyFrameIndex = timelineModel.getKeyFrames().indexOf(keyFrame);
        for (int i = keyFrameIndex + 1; i < timelineModel.getKeyFrames().size(); i++) {
            Object value = timelineModel.getKeyFrames().get(i).getKeyValues().get(node).get(field).getValue();
            if (value != null) {
                return timelineModel.getKeyFrames().get(i);
            }
        }
        return null;
    }

    private void unbind(NodeModel node) {
        Arrays.stream(AnimatableField.values()).forEach(f -> ModelFunctions.getDoubleProperty(node.getNode(), f).ifPresent(Property::unbind));
        Arrays.stream(AnimatableField.values()).forEach(f -> ModelFunctions.getPaintProperty(node.getNode(), f).ifPresent(Property::unbind));
    }
}
