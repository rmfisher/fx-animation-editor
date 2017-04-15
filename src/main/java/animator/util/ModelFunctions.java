package animator.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import animator.model.*;
import animator.model.interpolator.DefaultInterpolators;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModelFunctions {

    public static Stream<NodeModel> getAllSceneNodes(SceneModel sceneModel) {
        return sceneModel.getNodes().stream().flatMap(ModelFunctions::flatten);
    }

    public static Stream<NodeModel> flatten(NodeModel input) {
        Stream<NodeModel> nodes = Stream.of(input);
        if (input instanceof GroupModel) {
            return Stream.concat(nodes, ((GroupModel) input).getChildren().stream().flatMap(ModelFunctions::flatten));
        } else {
            return nodes;
        }
    }

    public static Optional<DoubleProperty> getDoubleProperty(Node node, AnimatableField field) {
        switch (field) {
            case LAYOUT_X:
                return Optional.of(node.layoutXProperty());
            case LAYOUT_Y:
                return Optional.of(node.layoutYProperty());
            case WIDTH:
                return optionalRectangle(node).map(Rectangle::widthProperty);
            case HEIGHT:
                return optionalRectangle(node).map(Rectangle::heightProperty);
            case STROKE_WIDTH:
                return optionalRectangle(node).map(Rectangle::strokeWidthProperty);
            case ARC_WIDTH:
                return optionalRectangle(node).map(Rectangle::arcWidthProperty);
            case ARC_HEIGHT:
                return optionalRectangle(node).map(Rectangle::arcHeightProperty);
            case OPACITY:
                return Optional.of(node.opacityProperty());
            case TRANSLATE_X:
                return Optional.of(node.translateXProperty());
            case TRANSLATE_Y:
                return Optional.of(node.translateYProperty());
            case TRANSLATE_Z:
                return Optional.of(node.translateZProperty());
            case ROTATE:
                return Optional.of(node.rotateProperty());
            case SCALE_X:
                return Optional.of(node.scaleXProperty());
            case SCALE_Y:
                return Optional.of(node.scaleYProperty());
            case SCALE_Z:
                return Optional.of(node.scaleZProperty());
            default:
                return Optional.empty();
        }
    }

    public static Optional<ObjectProperty<Paint>> getPaintProperty(Node node, AnimatableField field) {
        switch (field) {
            case FILL:
                return optionalRectangle(node).map(Rectangle::fillProperty);
            case STROKE:
                return optionalRectangle(node).map(Rectangle::strokeProperty);
            default:
                return Optional.empty();
        }
    }

    public static Map<AnimatableField, KeyValueModel> createKeyValues(NodeModel nodeModel) {
        Map<AnimatableField, KeyValueModel> keyValues = createNodeDoubles();
        if (nodeModel instanceof RectangleModel) {
            keyValues.putAll(createRectangleDoubles());
            keyValues.putAll(createPaints());
        }
        return keyValues;
    }

    public static Map<AnimatableField, KeyValueModel> createNodeDoubles() {
        Map<AnimatableField, KeyValueModel> map = new HashMap<>();
        add(map, AnimatableField.LAYOUT_X, 0.0);
        add(map, AnimatableField.LAYOUT_Y, 0.0);
        add(map, AnimatableField.OPACITY, 1.0);
        add(map, AnimatableField.TRANSLATE_X, 0.0);
        add(map, AnimatableField.TRANSLATE_Y, 0.0);
        add(map, AnimatableField.TRANSLATE_Z, 0.0);
        add(map, AnimatableField.ROTATE, 0.0);
        add(map, AnimatableField.SCALE_X, 1.0);
        add(map, AnimatableField.SCALE_Y, 1.0);
        add(map, AnimatableField.SCALE_Z, 1.0);
        return map;
    }

    public static Map<AnimatableField, KeyValueModel> createRectangleDoubles() {
        Map<AnimatableField, KeyValueModel> map = new HashMap<>();
        add(map, AnimatableField.WIDTH, 80.0);
        add(map, AnimatableField.HEIGHT, 80.0);
        add(map, AnimatableField.STROKE_WIDTH, 0.0);
        add(map, AnimatableField.ARC_WIDTH, 6.0);
        add(map, AnimatableField.ARC_HEIGHT, 6.0);
        map.putAll(createNodeDoubles());
        return map;
    }

    public static Map<AnimatableField, KeyValueModel> createPaints() {
        Map<AnimatableField, KeyValueModel> map = new HashMap<>();
        add(map, AnimatableField.FILL, Color.rgb(0, 180, 180));
        add(map, AnimatableField.STROKE, null);
        return map;
    }

    public static void add(Map<AnimatableField, KeyValueModel> map, AnimatableField field, Object initialValue) {
        KeyValueModel keyValueModel = new KeyValueModel(field);
        keyValueModel.setValue(initialValue);
        keyValueModel.setInterpolator(DefaultInterpolators.EASE_BOTH);
        map.put(field, keyValueModel);
    }

    public static Optional<Rectangle> optionalRectangle(Node node) {
        return Optional.of(node).filter(Rectangle.class::isInstance).map(Rectangle.class::cast);
    }

    public static Map<AnimatableField, KeyValueModel> deepCopy(Map<AnimatableField, KeyValueModel> source) {
        return source.values().stream().map(ModelFunctions::copy).collect(Collectors.toMap(KeyValueModel::getField, Function.identity()));
    }

    public static KeyValueModel copy(KeyValueModel source) {
        KeyValueModel keyValue = new KeyValueModel(source.getField());
        keyValue.setValue(source.getValue());
        keyValue.setInterpolator(source.getInterpolator());
        return keyValue;
    }
    
    public static void clear(Map<AnimatableField, KeyValueModel> properties) {
        properties.values().forEach(ModelFunctions::clear);
    }

    public static void clear(KeyValueModel keyValueModel) {
        keyValueModel.setValue(null);
        keyValueModel.setInterpolator(DefaultInterpolators.EASE_BOTH);
    }

    public static void bindDoubleBidirectional(ObjectProperty<Object> keyValueProperty, ObjectProperty<Double> doubleProperty) {
        doubleProperty.addListener((v, o, n) -> keyValueProperty.set(n));
        keyValueProperty.addListener((v, o, n) -> doubleProperty.set(n instanceof Double ? (Double) n : null));
        doubleProperty.set(keyValueProperty.get() instanceof Double ? (Double) keyValueProperty.get() : null);
    }

    public static void bindColorBidirectional(ObjectProperty<Object> keyValueProperty, ObjectProperty<Color> colorProperty) {
        colorProperty.addListener((v, o, n) -> keyValueProperty.set(n));
        keyValueProperty.addListener((v, o, n) -> colorProperty.set(n instanceof Color ? (Color) n : null));
        keyValueProperty.set(colorProperty.get());
    }

    public static DoubleBinding toDoubleBinding(ObjectProperty<Object> property) {
        return Bindings.createDoubleBinding(() -> (property.get() instanceof Double ? (Double) property.get() : 0), property);
    }

    public static ObjectBinding<Paint> toPaintBinding(ObjectProperty<Object> property, Paint fallback) {
        return Bindings.createObjectBinding(() -> (property.get() instanceof Paint ? (Paint) property.get() : fallback), property);
    }
}
