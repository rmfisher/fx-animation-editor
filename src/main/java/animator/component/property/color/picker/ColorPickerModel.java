package animator.component.property.color.picker;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class ColorPickerModel {

    private ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private DoubleProperty hue = new SimpleDoubleProperty();
    private DoubleProperty saturation = new SimpleDoubleProperty();
    private DoubleProperty brightness = new SimpleDoubleProperty();
    private IntegerProperty red = new SimpleIntegerProperty();
    private IntegerProperty green = new SimpleIntegerProperty();
    private IntegerProperty blue = new SimpleIntegerProperty();
    private DoubleProperty alpha = new SimpleDoubleProperty(1);

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public double getHue() {
        return hue.get();
    }

    public DoubleProperty hueProperty() {
        return hue;
    }

    public void setHue(double hue) {
        this.hue.set(hue);
    }

    public double getSaturation() {
        return saturation.get();
    }

    public DoubleProperty saturationProperty() {
        return saturation;
    }

    public void setSaturation(double saturation) {
        this.saturation.set(saturation);
    }

    public double getBrightness() {
        return brightness.get();
    }

    public DoubleProperty brightnessProperty() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness.set(brightness);
    }

    public int getRed() {
        return red.get();
    }

    public IntegerProperty redProperty() {
        return red;
    }

    public void setRed(int red) {
        this.red.set(red);
    }

    public int getGreen() {
        return green.get();
    }

    public IntegerProperty greenProperty() {
        return green;
    }

    public void setGreen(int green) {
        this.green.set(green);
    }

    public int getBlue() {
        return blue.get();
    }

    public IntegerProperty blueProperty() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue.set(blue);
    }

    public double getAlpha() {
        return alpha.get();
    }

    public DoubleProperty alphaProperty() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha.set(alpha);
    }
}
