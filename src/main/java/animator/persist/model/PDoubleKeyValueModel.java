package animator.persist.model;

import animator.model.AnimatableField;

public class PDoubleKeyValueModel {

    private AnimatableField field;
    private Double value;
    private int interpolator;

    public AnimatableField getField() {
        return field;
    }

    public void setField(AnimatableField field) {
        this.field = field;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(int interpolator) {
        this.interpolator = interpolator;
    }
}
