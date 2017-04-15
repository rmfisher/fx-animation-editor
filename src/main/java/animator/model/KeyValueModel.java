package animator.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import animator.model.interpolator.InterpolatorModel;

public class KeyValueModel {

    private final AnimatableField field;
    private final ObjectProperty<Object> value = new SimpleObjectProperty<>();
    private final ObjectProperty<InterpolatorModel> interpolator = new SimpleObjectProperty<>();

    public KeyValueModel(AnimatableField field) {
        this.field = field;
    }

    public AnimatableField getField() {
        return field;
    }

    public Object getValue() {
        return value.get();
    }

    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    public void setValue(Object value) {
        this.value.set(value);
    }

    public InterpolatorModel getInterpolator() {
        return interpolator.get();
    }

    public ObjectProperty<InterpolatorModel> interpolatorProperty() {
        return interpolator;
    }

    public void setInterpolator(InterpolatorModel interpolator) {
        this.interpolator.set(interpolator);
    }
}
