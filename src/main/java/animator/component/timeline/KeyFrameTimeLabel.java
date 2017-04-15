package animator.component.timeline;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import animator.component.util.text.ClampedDoubleStringConverter;
import animator.component.util.text.NonNegativeDoubleTextFormatter;
import animator.component.util.text.TextFieldHelper;

public class KeyFrameTimeLabel extends StackPane {

    // All times are in seconds.
    private static final double MIN_TIME = 0.01;
    private static final double MAX_TIME = 1e10;
    private static final double DEFAULT_TIME = 1;
    private static final String FORMAT = "%.2f";

    private final Label label = new Label();
    private final TextField textField = new TextField();
    private final DoubleProperty value = new SimpleDoubleProperty();
    private final ObjectProperty<Double> textFieldValue = new SimpleObjectProperty<>();
    private final BooleanProperty editMode = new SimpleBooleanProperty();

    private Runnable onCommit;

    public KeyFrameTimeLabel() {
        initUi();
        configureTextField();
        editMode.addListener((v, o, n) -> onEditModeChanged(n));
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

    public boolean isEditMode() {
        return editMode.get();
    }

    public BooleanProperty editModeProperty() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode.set(editMode);
    }

    public void setOnCommit(Runnable onCommit) {
        this.onCommit = onCommit;
    }

    private void initUi() {
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        getChildren().addAll(label, textField);
        textField.setLayoutX(-1);
        textField.setManaged(false);
        textField.setMouseTransparent(true);
        textField.setVisible(false);
        textFieldValue.bind(value.asObject());
        label.widthProperty().addListener((v, o, n) -> sizeTextField());
        label.heightProperty().addListener((v, o, n) -> sizeTextField());
        label.textProperty().bind(Bindings.createStringBinding(() -> String.format(FORMAT, value.get()), value));
    }

    private void sizeTextField() {
        textField.resize(label.getWidth() + 2, label.getHeight());
    }

    private void configureTextField() {
        ClampedDoubleStringConverter stringConverter = new ClampedDoubleStringConverter();
        stringConverter.setMin(MIN_TIME);
        stringConverter.setMax(MAX_TIME);
        stringConverter.setNullValue(DEFAULT_TIME);
        stringConverter.setDecimalPlaces(-1);
        stringConverter.setFormat(FORMAT);
        textField.setTextFormatter(new NonNegativeDoubleTextFormatter());
        textField.setContextMenu(new ContextMenu());
        TextFieldHelper.bind(textField, textFieldValue, stringConverter, null);
        TextFieldHelper.executeOnFocusLostOrEnter(textField, () -> {
            requestFocus();
            setEditMode(false);
            onCommit();
        });
    }

    private void onEditModeChanged(boolean newValue) {
        textField.setVisible(newValue);
        textField.setMouseTransparent(!newValue);
        label.setVisible(!newValue);
        if (newValue) {
            textFieldValue.unbind();
            textField.requestFocus();
        } else {
            requestFocus();
            value.set(textFieldValue.get());
            textFieldValue.bind(value.asObject());
        }
    }

    private void onCommit() {
        if (onCommit != null) {
            onCommit.run();
        }
    }
}
