package animator.event;

import animator.model.AnimatableField;
import animator.model.interpolator.InterpolatorModel;

public class InterpolatorEditEvent {

    private final AnimatableField field;
    private final InterpolatorModel interpolator;

    public InterpolatorEditEvent(AnimatableField field, InterpolatorModel interpolator) {
        this.field = field;
        this.interpolator = interpolator;
    }

    public AnimatableField getField() {
        return field;
    }

    public InterpolatorModel getInterpolator() {
        return interpolator;
    }
}
