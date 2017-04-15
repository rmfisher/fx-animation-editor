package animator.component.util.text;

import javafx.beans.property.*;

public class ClampedDoubleStringConverter extends SafeDoubleStringConverter {

    private final DoubleProperty min = new SimpleDoubleProperty(-Double.MAX_VALUE);
    private final DoubleProperty max = new SimpleDoubleProperty(Double.MAX_VALUE);
    private final ObjectProperty<Double> nullValue = new SimpleObjectProperty<>();
    private final IntegerProperty decimalPlaces = new SimpleIntegerProperty(2);

    private double roundingFactor;

    public ClampedDoubleStringConverter() {
        decimalPlaces.addListener((v, o, n) -> setRoundingFactor(n.intValue()));
        setRoundingFactor(decimalPlaces.get());
    }

    public double getMin() {
        return min.get();
    }

    public DoubleProperty minProperty() {
        return min;
    }

    public void setMin(double min) {
        this.min.set(min);
    }

    public double getMax() {
        return max.get();
    }

    public DoubleProperty maxProperty() {
        return max;
    }

    public void setMax(double max) {
        this.max.set(max);
    }

    public Double getNullValue() {
        return nullValue.get();
    }

    public ObjectProperty<Double> nullValueProperty() {
        return nullValue;
    }

    public void setNullValue(Double nullValue) {
        this.nullValue.set(nullValue);
    }

    public int getDecimalPlaces() {
        return decimalPlaces.get();
    }

    public IntegerProperty decimalPlacesProperty() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces) {
        this.decimalPlaces.set(decimalPlaces);
    }

    @Override
    public Double fromString(String string) {
        Double value = super.fromString(string);
        if (value == null && nullValue.get() == null) {
            return null;
        } else {
            double nonNull = value == null ? nullValue.get() : value;
            double rounded = round(nonNull);
            return rounded < min.get() ? min.get() : rounded > max.get() ? max.get() : rounded;
        }
    }

    @Override
    public String toString(Double value) {
        Double rounded = value != null ? round(value) : null;
        return super.toString(rounded);
    }

    private void setRoundingFactor(int decimalPlaces) {
        roundingFactor = decimalPlaces >= 0 ? Math.pow(10, decimalPlaces) : -1;
    }

    private double round(double value) {
        if (roundingFactor > 0) {
            return Math.round(value * roundingFactor) / roundingFactor;
        } else {
            return value;
        }
    }
}
