package animator.persist;

import javafx.scene.paint.Color;
import animator.model.*;
import animator.model.interpolator.InterpolationParameter;
import animator.model.interpolator.InterpolatorListModel;
import animator.model.interpolator.InterpolatorModel;
import animator.persist.model.*;
import animator.persist.model.interpolator.PInterpolatorListModel;
import animator.util.MapFunctions;
import animator.util.ModelFunctions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ModelConverter {

    public static PAnimationModel fromFx(TimelineModel timelineModel, SceneModel sceneModel, InterpolatorListModel interpolators) {
        PAnimationModel model = new PAnimationModel();
        Map<NodeModel, Integer> idMap = createIdMap(sceneModel);
        model.setScene(fromFx(sceneModel, idMap));
        model.setTimeline(fromFx(timelineModel, idMap, interpolators));
        model.setInterpolatorList(fromFx(interpolators));
        return model;
    }

    public static void toFx(TimelineModel timelineModel, SceneModel sceneModel, InterpolatorListModel interpolators, PAnimationModel persistedModel) {
        Map<Integer, NodeModel> idMap = new HashMap<>();
        toFx(sceneModel, persistedModel.getScene(), idMap);
        toFx(timelineModel, persistedModel.getTimeline(), idMap, interpolators);
        toFx(interpolators, persistedModel.getInterpolatorList());
    }

    private static PSceneModel fromFx(SceneModel input, Map<NodeModel, Integer> idMap) {
        PSceneModel converted = new PSceneModel();
        converted.setNodes(input.getNodes().stream().map(n -> fromFx(n, idMap)).collect(toList()));
        converted.setSnapToGrid(input.isSnapToGrid());
        return converted;
    }

    private static PNodeModel fromFx(NodeModel input, Map<NodeModel, Integer> idMap) {
        PNodeModel converted = new PNodeModel();
        converted.setId(idMap.get(input));
        if (input instanceof RectangleModel) {
            converted.setType(PNodeType.RECTANGLE);
        } else if (input instanceof GroupModel) {
            GroupModel groupModel = (GroupModel) input;
            converted.setType(PNodeType.GROUP);
            converted.setChildren(groupModel.getChildren().stream().map(n -> fromFx(n, idMap)).collect(toList()));
        } else {
            throw new UnsupportedOperationException();
        }
        return converted;
    }

    private static PTimelineModel fromFx(TimelineModel input, Map<NodeModel, Integer> idMap, InterpolatorListModel interpolators) {
        PTimelineModel converted = new PTimelineModel();
        converted.setKeyFrames(input.getKeyFrames().stream().map(k -> fromFx(k, idMap, interpolators)).collect(toList()));
        return converted;
    }

    private static PKeyFrameModel fromFx(KeyFrameModel input, Map<NodeModel, Integer> idMap, InterpolatorListModel interpolators) {
        PKeyFrameModel converted = new PKeyFrameModel();
        converted.setTime(input.getTime());
        converted.setNodeStates(MapFunctions.map(input.getKeyValues(), (k, v) -> fromFx(idMap.get(k), v, interpolators)).collect(toList()));
        return converted;
    }

    private static PNodeStateModel fromFx(int nodeId, Map<AnimatableField, KeyValueModel> keyValues, InterpolatorListModel interpolators) {
        PNodeStateModel converted = new PNodeStateModel();
        converted.setNodeId(nodeId);
        converted.setDoubleKeyValues(keyValues.values().stream().filter(v -> !isColor(v)).map(v -> fxToDouble(v, interpolators)).collect(toList()));
        converted.setColorKeyValues(keyValues.values().stream().filter(ModelConverter::isColor).map(v -> fxToColor(v, interpolators)).collect(toList()));
        return converted;
    }

    private static boolean isColor(KeyValueModel keyValueModel) {
        return keyValueModel.getField() == AnimatableField.FILL || keyValueModel.getField() == AnimatableField.STROKE;
    }

    private static PDoubleKeyValueModel fxToDouble(KeyValueModel input, InterpolatorListModel interpolators) {
        PDoubleKeyValueModel converted = new PDoubleKeyValueModel();
        converted.setField(input.getField());
        converted.setValue((Double) input.getValue());
        converted.setInterpolator(interpolators.getInterpolators().indexOf(input.getInterpolator()));
        return converted;
    }

    private static PColorKeyValueModel fxToColor(KeyValueModel input, InterpolatorListModel interpolators) {
        PColorKeyValueModel converted = new PColorKeyValueModel();
        converted.setField(input.getField());
        converted.setValue(input.getValue() != null ? fromFx((Color) input.getValue()) : null);
        converted.setInterpolator(interpolators.getInterpolators().indexOf(input.getInterpolator()));
        return converted;
    }

    private static PColorModel fromFx(Color value) {
        PColorModel converted = new PColorModel();
        converted.setRed(value.getRed());
        converted.setGreen(value.getGreen());
        converted.setBlue(value.getBlue());
        converted.setAlpha(value.getOpacity());
        return converted;
    }

    private static PInterpolatorListModel fromFx(InterpolatorListModel interpolators) {
        InterpolatorModel spline = interpolators.getInterpolators().get(4);
        InterpolatorModel overshoot = interpolators.getInterpolators().get(5);
        InterpolatorModel bounce = interpolators.getInterpolators().get(6);

        PInterpolatorListModel converted = new PInterpolatorListModel();
        converted.setSplineParameters(spline.getParameters().stream().map(InterpolationParameter::getValue).collect(toList()));
        converted.setOvershootParameters(overshoot.getParameters().stream().map(InterpolationParameter::getValue).collect(toList()));
        converted.setBounceParameters(bounce.getParameters().stream().map(InterpolationParameter::getValue).collect(toList()));
        return converted;
    }

    private static void toFx(SceneModel sceneModel, PSceneModel input, Map<Integer, NodeModel> idMap) {
        sceneModel.getNodes().forEach(n -> n.setSelected(false));
        sceneModel.getNodes().clear();
        sceneModel.getNodes().setAll(input.getNodes().stream().map(n -> toFx(n, null, idMap)).collect(Collectors.toList()));
        sceneModel.setSnapToGrid(input.isSnapToGrid());
    }

    private static NodeModel toFx(PNodeModel input, GroupModel parent, Map<Integer, NodeModel> idMap) {
        if (input.getType() == PNodeType.RECTANGLE) {
            RectangleModel rectangleModel = new RectangleModel();
            rectangleModel.setGroup(parent);
            idMap.put(input.getId(), rectangleModel);
            return rectangleModel;
        } else if (input.getType() == PNodeType.GROUP) {
            GroupModel groupModel = new GroupModel();
            groupModel.getChildren().setAll(input.getChildren().stream().map(n -> toFx(n, groupModel, idMap)).collect(Collectors.toList()));
            groupModel.setGroup(parent);
            idMap.put(input.getId(), groupModel);
            return groupModel;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private static void toFx(TimelineModel timelineModel, PTimelineModel input, Map<Integer, NodeModel> idMap, InterpolatorListModel interpolators) {
        timelineModel.setSelectedKeyFrame(null);
        timelineModel.getKeyFrames().clear();
        timelineModel.getKeyFrames().setAll(input.getKeyFrames().stream().map(k -> toFx(k, idMap, interpolators)).collect(Collectors.toList()));
        timelineModel.setSelectedKeyFrame(timelineModel.getKeyFrames().get(0));
    }

    private static KeyFrameModel toFx(PKeyFrameModel input, Map<Integer, NodeModel> idMap, InterpolatorListModel interpolators) {
        KeyFrameModel keyFrameModel = new KeyFrameModel();
        keyFrameModel.setTime(input.getTime());
        keyFrameModel.getKeyValues().putAll(input.getNodeStates().stream().collect(toMap(s -> idMap.get(s.getNodeId()), k -> toFx(k, interpolators))));
        return keyFrameModel;
    }

    private static Map<AnimatableField, KeyValueModel> toFx(PNodeStateModel input, InterpolatorListModel interpolators) {
        Map<AnimatableField, KeyValueModel> converted = new HashMap<>();
        converted.putAll(input.getDoubleKeyValues().stream().collect(toMap(PDoubleKeyValueModel::getField, k -> toFx(k, interpolators))));
        converted.putAll(input.getColorKeyValues().stream().collect(toMap(PColorKeyValueModel::getField, k -> toFx(k, interpolators))));
        return converted;
    }

    private static KeyValueModel toFx(PDoubleKeyValueModel input, InterpolatorListModel interpolators) {
        KeyValueModel converted = new KeyValueModel(input.getField());
        converted.setValue(input.getValue());
        converted.setInterpolator(interpolators.getInterpolators().get(input.getInterpolator()));
        return converted;
    }

    private static KeyValueModel toFx(PColorKeyValueModel input, InterpolatorListModel interpolators) {
        KeyValueModel converted = new KeyValueModel(input.getField());
        if (input.getValue() != null) {
            converted.setValue(new Color(input.getValue().getRed(), input.getValue().getGreen(), input.getValue().getBlue(), input.getValue().getAlpha()));
        }
        converted.setInterpolator(interpolators.getInterpolators().get(input.getInterpolator()));
        return converted;
    }

    private static void toFx(InterpolatorListModel interpolators, PInterpolatorListModel input) {
        InterpolatorModel spline = interpolators.getInterpolators().get(4);
        InterpolatorModel overshoot = interpolators.getInterpolators().get(5);
        InterpolatorModel bounce = interpolators.getInterpolators().get(6);

        spline.getParameters().forEach(p -> p.setValue(input.getSplineParameters().get(spline.getParameters().indexOf(p))));
        overshoot.getParameters().forEach(p -> p.setValue(input.getOvershootParameters().get(overshoot.getParameters().indexOf(p))));
        bounce.getParameters().forEach(p -> p.setValue(input.getBounceParameters().get(bounce.getParameters().indexOf(p))));
    }

    private static Map<NodeModel, Integer> createIdMap(SceneModel sceneModel) {
        List<NodeModel> allNodes = ModelFunctions.getAllSceneNodes(sceneModel).collect(Collectors.toList());
        return allNodes.stream().collect(toMap(Function.identity(), allNodes::indexOf));
    }
}