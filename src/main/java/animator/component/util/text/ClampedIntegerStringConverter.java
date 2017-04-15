package animator.component.util.text;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.converter.IntegerStringConverter;

public class ClampedIntegerStringConverter extends IntegerStringConverter {

    private final IntegerProperty min = new SimpleIntegerProperty();
    private final IntegerProperty max = new SimpleIntegerProperty(Integer.MAX_VALUE);

    public int getMin() {
        return min.get();
    }

    public IntegerProperty minProperty() {
        return min;
    }

    public void setMin(int min) {
        this.min.set(min);
    }

    public int getMax() {
        return max.get();
    }

    public IntegerProperty maxProperty() {
        return max;
    }

    public void setMax(int max) {
        this.max.set(max);
    }

    @Override
    public Integer fromString(String value) {
        Integer raw = super.fromString(value);
        return raw == null || raw < min.get() ? min.get() : raw > max.get() ? max.get() : raw;
    }
}
