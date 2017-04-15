package animator.command;

import animator.model.KeyValueModel;

public class KeyValueChange {

    private KeyValueModel keyValue;
    private final Object oldValue;
    private final Object newValue;

    public KeyValueChange(KeyValueModel keyValue, Object oldValue, Object newValue) {
        this.keyValue = keyValue;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public KeyValueModel getKeyValue() {
        return keyValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}