package animator.model.interpolator;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class InterpolationParameter {

    private final String name;
    private final double min;
    private final double max;
    private final int decimalPlaces;
    private final double blockIncrement;
    private final double defaultValue;
    private final DoubleProperty value = new SimpleDoubleProperty();
    private final ObjectProperty<Double> valueAsObject = value.asObject();

    public InterpolationParameter(String name, double min, double max, int decimalPlaces, double blockIncrement, double defaultValue) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.decimalPlaces = decimalPlaces;
        this.blockIncrement = blockIncrement;
        this.defaultValue = defaultValue;
        this.value.set(defaultValue);
    }

    public String getName() {
        return name;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public double getBlockIncrement() {
        return blockIncrement;
    }

    public double getDefaultValue() {
        return defaultValue;
    }

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    public ObjectProperty<Double> valueAsObject() {
        return valueAsObject;
    }
}
