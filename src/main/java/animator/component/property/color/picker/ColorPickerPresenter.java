package animator.component.property.color.picker;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.*;
import animator.component.util.text.ClampedDoubleStringConverter;
import animator.component.util.text.ClampedIntegerStringConverter;
import animator.component.util.text.NonNegativeDoubleTextFormatter;
import animator.component.util.text.TextFieldHelper;

import javax.inject.Inject;

import static javafx.beans.binding.Bindings.createObjectBinding;
import static animator.component.util.binding.BindingFunctions.round;

public class ColorPickerPresenter {

    private static final String VALUE_AS_OBJECT_KEY = "value-as-object";

    private final BackgroundFill whiteToTransparentAcross = createGradientFill(1, 0, Color.WHITE, Color.TRANSPARENT);
    private final BackgroundFill transparentToBlackDown = createGradientFill(0, 1, Color.TRANSPARENT, Color.BLACK);
    private final ClampedIntegerStringConverter rgbStringConverter = new ClampedIntegerStringConverter();
    private final ClampedDoubleStringConverter alphaStringConverter = new ClampedDoubleStringConverter();
    private final ColorPickerModel model;

    private ColorPickerComponent view;
    private boolean updating;
    private Runnable onChangeStarted;
    private Runnable onChanged;
    private Runnable onChangeFinished;
    private Runnable onCommit;

    @Inject
    public ColorPickerPresenter(ColorPickerModel model) {
        this.model = model;
        rgbStringConverter.setMin(0);
        rgbStringConverter.setMax(255);
        alphaStringConverter.setMin(0);
        alphaStringConverter.setMax(1);
        alphaStringConverter.setNullValue(1.0);
        alphaStringConverter.setDecimalPlaces(2);
    }

    public ObjectProperty<Color> colorProperty() {
        return model.colorProperty();
    }

    void setOnChangeStarted(Runnable onChangeStarted) {
        this.onChangeStarted = onChangeStarted;
    }

    void setOnChanged(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    void setOnChangeFinished(Runnable onChangeFinished) {
        this.onChangeFinished = onChangeFinished;
    }

    void setOnCommit(Runnable onCommit) {
        this.onCommit = onCommit;
    }

    void setView(ColorPickerComponent view) {
        this.view = view;
        bindModelAndView();
        addPaletteMouseHandlers();
        addColorSyncListeners();
        configureTextFields();
    }

    private void bindModelAndView() {
        view.getSaturationAndBrightnessPane().backgroundProperty().bind(createObjectBinding(this::createBackground, model.hueProperty()));
        view.getSaturationAndBrightnessPane().opacityProperty().bind(model.alphaProperty());
        view.getSaturationAndBrightnessIndicator().layoutXProperty().bind(getSaturationX());
        view.getSaturationAndBrightnessIndicator().layoutYProperty().bind(getBrightnessY());
        view.getHueIndicator().layoutXProperty().bind(getHueX());
        bindRgbField(view.getRedTextField(), model.redProperty());
        bindRgbField(view.getGreenTextField(), model.greenProperty());
        bindRgbField(view.getBlueTextField(), model.blueProperty());
        bindAlphaField();
    }

    private Background createBackground() {
        BackgroundFill hueFill = new BackgroundFill(Color.hsb(model.getHue(), 1, 1), null, null);
        return new Background(hueFill, whiteToTransparentAcross, transparentToBlackDown);
    }

    private NumberBinding getSaturationX() {
        return round(model.saturationProperty().multiply(view.getSaturationAndBrightnessPane().widthProperty()));
    }

    private DoubleBinding getBrightnessY() {
        NumberBinding oneMinusBrightness = model.brightnessProperty().multiply(-1).add(1);
        return round(oneMinusBrightness.multiply(view.getSaturationAndBrightnessPane().heightProperty()));
    }

    private DoubleBinding getHueX() {
        return round(model.hueProperty().divide(360).multiply(view.getHuePane().widthProperty()));
    }

    private void addPaletteMouseHandlers() {
        view.getSaturationAndBrightnessPane().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onSBPressed);
        view.getSaturationAndBrightnessPane().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onSBDragged);
        view.getSaturationAndBrightnessPane().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> notifyChangeFinished());
        view.getHuePane().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onHuePressed);
        view.getHuePane().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onHueDragged);
        view.getHuePane().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> notifyChangeFinished());
    }

    private void onSBPressed(MouseEvent event) {
        adjustSaturationAndBrightness(event);
        notifyChangeStarted();
        notifyChanged();
    }

    private void onSBDragged(MouseEvent event) {
        adjustSaturationAndBrightness(event);
        notifyChanged();
    }

    private void onHuePressed(MouseEvent event) {
        adjustHue(event);
        notifyChangeStarted();
        notifyChanged();
    }

    private void onHueDragged(MouseEvent event) {
        adjustHue(event);
        notifyChanged();
    }

    private void adjustSaturationAndBrightness(MouseEvent event) {
        model.setSaturation(clamp(event.getX() / view.getSaturationAndBrightnessPane().getWidth()));
        model.setBrightness(clamp(1 - event.getY() / view.getSaturationAndBrightnessPane().getHeight()));
    }

    private void adjustHue(MouseEvent event) {
        model.setHue(clamp(event.getX() / view.getHuePane().getWidth()) * 360);
    }

    private void addColorSyncListeners() {
        ChangeListener<Number> hsbListener = (v, o, n) -> onHsbChanged();
        ChangeListener<Number> rgbListener = (v, o, n) -> onRgbChanged();
        ChangeListener<Color> colorListener = (v, o, n) -> onColorChanged();
        model.hueProperty().addListener(hsbListener);
        model.saturationProperty().addListener(hsbListener);
        model.brightnessProperty().addListener(hsbListener);
        model.alphaProperty().addListener(hsbListener);
        model.redProperty().addListener(rgbListener);
        model.greenProperty().addListener(rgbListener);
        model.blueProperty().addListener(rgbListener);
        model.colorProperty().addListener(colorListener);
    }

    private void onHsbChanged() {
        if (!updating) {
            updating = true;
            updateColorFromHsb();
            updateRgbFromColor();
            updating = false;
        }
    }

    private void onRgbChanged() {
        if (!updating) {
            updating = true;
            updateColorFromRgb();
            updateHsbFromColor();
            updating = false;
        }
    }

    private void onColorChanged() {
        if (!updating) {
            updating = true;
            updateHsbFromColor();
            updateRgbFromColor();
            updating = false;
        }
    }

    private void updateColorFromHsb() {
        model.setColor(Color.hsb(model.getHue(), model.getSaturation(), model.getBrightness(), model.getAlpha()));
    }

    private void updateColorFromRgb() {
        model.setColor(Color.rgb(model.getRed(), model.getGreen(), model.getBlue(), model.getAlpha()));
    }

    private void updateHsbFromColor() {
        if (model.getColor() != null) {
            model.setHue(model.getColor().getHue());
            model.setSaturation(model.getColor().getSaturation());
            model.setBrightness(model.getColor().getBrightness());
            model.setAlpha(roundAlpha(model.getColor().getOpacity()));
        }
    }

    private void updateRgbFromColor() {
        if (model.getColor() != null) {
            model.setRed(toInt(model.getColor().getRed()));
            model.setGreen(toInt(model.getColor().getGreen()));
            model.setBlue(toInt(model.getColor().getBlue()));
            model.setAlpha(roundAlpha(model.getColor().getOpacity()));
        }
    }

    private void configureTextFields() {
        view.getRedTextField().setTextFormatter(new RgbTextFormatter());
        view.getGreenTextField().setTextFormatter(new RgbTextFormatter());
        view.getBlueTextField().setTextFormatter(new RgbTextFormatter());
        view.getAlphaTextField().setTextFormatter(new NonNegativeDoubleTextFormatter());
    }

    private void bindRgbField(TextField textField, IntegerProperty modelValue) {
        ObjectProperty<Integer> asObject = modelValue.asObject();
        textField.getProperties().put(VALUE_AS_OBJECT_KEY, asObject); // Prevents garbage collection.
        TextFieldHelper.bind(textField, asObject, rgbStringConverter, this::notifyCommit);
    }

    private void bindAlphaField() {
        ObjectProperty<Double> asObject = model.alphaProperty().asObject();
        view.getAlphaTextField().getProperties().put(VALUE_AS_OBJECT_KEY, asObject);
        TextFieldHelper.bind(view.getAlphaTextField(), asObject, alphaStringConverter, this::notifyCommit);
    }

    private static BackgroundFill createGradientFill(double endX, double endY, Color startColor, Color endColor) {
        Paint gradient = new LinearGradient(0, 0, endX, endY, true, CycleMethod.NO_CYCLE, new Stop(0, startColor), new Stop(1, endColor));
        return new BackgroundFill(gradient, null, null);
    }

    private static double clamp(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value;
    }

    private static int toInt(double colorValue) {
        return (int) Math.round(colorValue * 255);
    }

    private static double roundAlpha(double value) {
        return Math.round(value * 100) / 100.0;
    }

    private void notifyChangeStarted() {
        if (onChangeStarted != null) {
            onChangeStarted.run();
        }
    }

    private void notifyChanged() {
        if (onChanged != null) {
            onChanged.run();
        }
    }

    private void notifyChangeFinished() {
        if (onChangeFinished != null) {
            onChangeFinished.run();
        }
    }

    private void notifyCommit() {
        if (onCommit != null) {
            onCommit.run();
        }
    }
}
