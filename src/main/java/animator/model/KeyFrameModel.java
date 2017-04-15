package animator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.HashMap;
import java.util.Map;

public class KeyFrameModel {

    private final Map<NodeModel, Map<AnimatableField, KeyValueModel>> keyValues = new HashMap<>();
    private final DoubleProperty time = new SimpleDoubleProperty();
    private final DoubleProperty absoluteTime = new SimpleDoubleProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public Map<NodeModel, Map<AnimatableField, KeyValueModel>> getKeyValues() {
        return keyValues;
    }

    public double getTime() {
        return time.get();
    }

    public DoubleProperty timeProperty() {
        return time;
    }

    public void setTime(double time) {
        this.time.set(time);
    }

    public double getAbsoluteTime() {
        return absoluteTime.get();
    }

    public DoubleProperty absoluteTimeProperty() {
        return absoluteTime;
    }

    public void setAbsoluteTime(double absoluteTime) {
        this.absoluteTime.set(absoluteTime);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
