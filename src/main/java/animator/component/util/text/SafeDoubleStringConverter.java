package animator.component.util.text;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.StringConverter;

public class SafeDoubleStringConverter extends StringConverter<Double> {

    private final StringProperty format = new SimpleStringProperty();

    public String getFormat() {
        return format.get();
    }

    public StringProperty formatProperty() {
        return format;
    }

    public void setFormat(String format) {
        this.format.set(format);
    }

    @Override
    public Double fromString(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.isEmpty()) {
            return null;
        }
        return DoubleParser.parse(value);
    }

    @Override
    public String toString(Double value) {
        if (value == null) {
            return "";
        }
        return format.get() != null ? String.format(format.get(), value) : Double.toString(value);
    }
}
