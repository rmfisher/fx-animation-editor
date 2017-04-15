package animator.command;

import animator.model.interpolator.InterpolatorModel;
import animator.model.KeyValueModel;

public class InterpolatorChange {

    private final KeyValueModel keyValue;
    private final InterpolatorModel oldValue;
    private final InterpolatorModel newValue;

    public InterpolatorChange(KeyValueModel keyValue, InterpolatorModel oldValue, InterpolatorModel newValue) {
        this.keyValue = keyValue;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public KeyValueModel getKeyValue() {
        return keyValue;
    }

    public InterpolatorModel getOldValue() {
        return oldValue;
    }

    public InterpolatorModel getNewValue() {
        return newValue;
    }
}
