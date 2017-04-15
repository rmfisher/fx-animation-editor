package animator.component.property.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SliderComponent extends DoubleInputComponent {

    private static final String STYLE_CLASS = "slider-component";
    private static final String SLIDER_BOX_STYLE_CLASS = "slider-box";

    private final Slider slider = new Slider();
    private final ObjectProperty<Double> sliderValueAsObject = slider.valueProperty().asObject();

    private Runnable onChangeStarted;
    private Runnable onChange;
    private Runnable onChangeFinished;

    public SliderComponent() {
        super();
        createUi();
        getStringConverter().setMin(0);
        getStringConverter().setMax(1);
    }

    public double getBlockIncrement() {
        return slider.getBlockIncrement();
    }

    public DoubleProperty blockIncrementProperty() {
        return slider.blockIncrementProperty();
    }

    public void setBlockIncrement(double value) {
        slider.setBlockIncrement(value);
    }

    public void setOnChangeStarted(Runnable onChangeStarted) {
        this.onChangeStarted = onChangeStarted;
    }

    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    public void setOnChangeFinished(Runnable onChangeFinished) {
        this.onChangeFinished = onChangeFinished;
    }

    private void createUi() {
        getStyleClass().add(STYLE_CLASS);

        HBox innerBox = new HBox(getInputBox().getChildren().remove(0), slider);
        innerBox.getStyleClass().add(SLIDER_BOX_STYLE_CLASS);
        innerBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(innerBox, Priority.ALWAYS);
        getInputBox().getChildren().add(0, innerBox);

        slider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(slider, Priority.ALWAYS);

        getTextField().setMaxWidth(USE_PREF_SIZE);
        getTextField().setMinWidth(USE_PREF_SIZE);
        HBox.setHgrow(getTextField(), Priority.NEVER);

        sliderValueAsObject.bindBidirectional(valueProperty());
        slider.minProperty().bind(getStringConverter().minProperty());
        slider.maxProperty().bind(getStringConverter().maxProperty());
        slider.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> onPressed()); // Want this to fire before slider value changes.
        slider.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> onDragged());
        slider.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> onReleased());
    }

    private void onPressed() {
        if (onChangeStarted != null) {
            onChangeStarted.run();
        }
        if (onChange != null) {
            onChange.run();
        }
    }

    private void onDragged() {
        if (onChange != null) {
            onChange.run();
        }
    }

    private void onReleased() {
        if (onChangeFinished != null) {
            onChangeFinished.run();
        }
    }
}
