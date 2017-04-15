package animator.component.property.control;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import animator.component.util.text.ClampedDoubleStringConverter;
import animator.component.util.text.DoubleTextFormatter;
import animator.component.util.text.NonNegativeDoubleTextFormatter;
import animator.component.util.text.TextFieldHelper;
import animator.model.interpolator.InterpolatorModel;

public class DoubleInputComponent extends TextInputComponent {

    private final ObjectProperty<Double> value = new SimpleObjectProperty<>();
    private final ClampedDoubleStringConverter stringConverter = new ClampedDoubleStringConverter();
    private final InterpolatorButtonComponent interpolatorButton = new InterpolatorButtonComponent();

    private Runnable onCommit;

    public DoubleInputComponent() {
        createUi();
    }

    public Double getValue() {
        return value.get();
    }

    public ObjectProperty<Double> valueProperty() {
        return value;
    }

    public void setValue(Double value) {
        this.value.set(value);
    }

    public double getMax() {
        return stringConverter.getMax();
    }

    public DoubleProperty maxProperty() {
        return stringConverter.maxProperty();
    }

    public void setMax(double value) {
        stringConverter.setMax(value);
    }

    public double getMin() {
        return stringConverter.getMin();
    }

    public DoubleProperty minProperty() {
        return stringConverter.minProperty();
    }

    public void setMin(double value) {
        stringConverter.setMin(value);
    }

    public Double getNullValue() {
        return stringConverter.getNullValue();
    }

    public ObjectProperty<Double> nullValueProperty() {
        return stringConverter.nullValueProperty();
    }

    public void setNullValue(Double nullValue) {
        stringConverter.setNullValue(nullValue);
    }

    public int getDecimalPlaces() {
        return stringConverter.getDecimalPlaces();
    }

    public IntegerProperty decimalPlacesProperty() {
        return stringConverter.decimalPlacesProperty();
    }

    public void setDecimalPlaces(int decimalPlaces) {
        stringConverter.setDecimalPlaces(decimalPlaces);
    }

    public ObservableList<InterpolatorModel> getInterpolators() {
        return interpolatorButton.getItems();
    }

    public ListProperty<InterpolatorModel> interpolatorsProperty() {
        return interpolatorButton.itemsProperty();
    }

    public void setInterpolators(ObservableList<InterpolatorModel> interpolators) {
        this.interpolatorButton.setItems(interpolators);
    }

    public InterpolatorModel getSelectedInterpolator() {
        return interpolatorButton.selectionProperty().get();
    }

    public ObjectProperty<InterpolatorModel> selectedInterpolatorProperty() {
        return interpolatorButton.selectionProperty();
    }

    public void setSelectedInterpolator(InterpolatorModel selection) {
        interpolatorButton.selectionProperty().set(selection);
    }

    public boolean isInterpolatorButtonVisible() {
        return interpolatorButton.visibleProperty().get();
    }

    public BooleanProperty interpolatorButtonVisibleProperty() {
        return interpolatorButton.visibleProperty();
    }

    public void setInterpolatorButtonVisible(boolean visible) {
        interpolatorButton.setVisible(visible);
    }

    public void setOnCommit(Runnable onCommit) {
        this.onCommit = onCommit;
    }

    public void setOnInterpolatorSelected(Runnable onInterpolatorSelected) {
        interpolatorButton.setOnItemSelected(onInterpolatorSelected);
    }

    protected ClampedDoubleStringConverter getStringConverter() {
        return stringConverter;
    }

    private void createUi() {
        getTextField().setTextFormatter(new DoubleTextFormatter());
        TextFieldHelper.bind(getTextField(), value, stringConverter, this::onCommit);

        getInputBox().getChildren().add(interpolatorButton);
        interpolatorButton.managedProperty().bind(interpolatorButton.visibleProperty());

        minProperty().addListener((v, o, n) -> onMinChanged(n.doubleValue()));
        getClearButton().setOnAction(event -> resetAndCommit());
    }

    private void onCommit() {
        if (onCommit != null) {
            onCommit.run();
        }
    }

    private void onMinChanged(double newValue) {
        getTextField().setTextFormatter(newValue < 0 ? new DoubleTextFormatter() : new NonNegativeDoubleTextFormatter());
    }

    private void resetAndCommit() {
        getTextField().setText(stringConverter.toString(stringConverter.getNullValue()));
        getTextField().fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, null, null, KeyCode.ENTER, false, false, false, false));
    }
}