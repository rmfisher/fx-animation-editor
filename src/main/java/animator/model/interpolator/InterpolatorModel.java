package animator.model.interpolator;

import javafx.animation.Interpolator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.function.Supplier;

public abstract class InterpolatorModel {

    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Supplier<Node>> iconSupplier = new SimpleObjectProperty<>();
    private final ObservableList<InterpolationParameter> parameters = FXCollections.observableArrayList();

    public ObservableList<InterpolationParameter> getParameters() {
        return parameters;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Supplier<Node> getIconSupplier() {
        return iconSupplier.get();
    }

    public ObjectProperty<Supplier<Node>> iconSupplierProperty() {
        return iconSupplier;
    }

    public void setIconSupplier(Supplier<Node> iconSupplier) {
        this.iconSupplier.set(iconSupplier);
    }

    public abstract double curve(double t);

    public abstract Interpolator toFxInterpolator();

    protected static double clamp(double t) {
        return (t < 0.0) ? 0.0 : (t > 1.0) ? 1.0 : t;
    }
}
