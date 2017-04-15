package animator.event;

import animator.model.AnimatableField;

public class PropertyEditEvent {

    private final Type type;
    private final AnimatableField field;
    private final Object newValue;

    public PropertyEditEvent(Type type, AnimatableField field, Object newValue) {
        this.type = type;
        this.field = field;
        this.newValue = newValue;
    }

    public Type getType() {
        return type;
    }

    public AnimatableField getField() {
        return field;
    }

    public Object getNewValue() {
        return newValue;
    }

    public enum Type {
        START, UPDATE, FINISH, SINGLE
    }
}
