package animator.component.property.color;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import animator.model.interpolator.InterpolatorModel;

public class ColorEditorModel {

    private final ObjectProperty<Color> fill = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> stroke = new SimpleObjectProperty<>();
    private final ObjectProperty<InterpolatorModel> fillInterpolator = new SimpleObjectProperty<>();
    private final ObjectProperty<InterpolatorModel> strokeInterpolator = new SimpleObjectProperty<>();
    private final BooleanProperty strokeSelected = new SimpleBooleanProperty();

    public Color getFill() {
        return fill.get();
    }

    public ObjectProperty<Color> fillProperty() {
        return fill;
    }

    public void setFill(Color fill) {
        this.fill.set(fill);
    }

    public Color getStroke() {
        return stroke.get();
    }

    public ObjectProperty<Color> strokeProperty() {
        return stroke;
    }

    public void setStroke(Color stroke) {
        this.stroke.set(stroke);
    }

    public InterpolatorModel getFillInterpolator() {
        return fillInterpolator.get();
    }

    public ObjectProperty<InterpolatorModel> fillInterpolatorProperty() {
        return fillInterpolator;
    }

    public void setFillInterpolator(InterpolatorModel fillInterpolator) {
        this.fillInterpolator.set(fillInterpolator);
    }

    public InterpolatorModel getStrokeInterpolator() {
        return strokeInterpolator.get();
    }

    public ObjectProperty<InterpolatorModel> strokeInterpolatorProperty() {
        return strokeInterpolator;
    }

    public void setStrokeInterpolator(InterpolatorModel strokeInterpolator) {
        this.strokeInterpolator.set(strokeInterpolator);
    }

    public boolean isStrokeSelected() {
        return strokeSelected.get();
    }

    public BooleanProperty strokeSelectedProperty() {
        return strokeSelected;
    }

    public void setStrokeSelected(boolean strokeSelected) {
        this.strokeSelected.set(strokeSelected);
    }
}
