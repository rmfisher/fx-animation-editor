package animator.persist.model;

import animator.model.AnimatableField;

public class PColorKeyValueModel {

    private AnimatableField field;
    private PColorModel value;
    private int interpolator;

    public AnimatableField getField() {
        return field;
    }

    public void setField(AnimatableField field) {
        this.field = field;
    }

    public PColorModel getValue() {
        return value;
    }

    public void setValue(PColorModel value) {
        this.value = value;
    }

    public int getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(int interpolator) {
        this.interpolator = interpolator;
    }
}
