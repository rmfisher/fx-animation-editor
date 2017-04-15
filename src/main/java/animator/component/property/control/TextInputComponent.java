package animator.component.property.control;

import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import animator.component.util.icon.Svg;

public class TextInputComponent extends GridPane {

    private static final String STYLE_CLASS = "text-input-component";
    private static final String INPUT_BOX_STYLE_CLASS = "input-box";
    private static final String CLEAR_BUTTON_STYLE_CLASS = "clear-button";
    private static final double INITIAL_LABEL_PERCENT_WIDTH = 35;

    private final Label label = new Label();
    private final TextField textField = new TextField();
    private final HBox inputBox = new HBox();
    private final DoubleProperty labelWidth = new SimpleDoubleProperty(INITIAL_LABEL_PERCENT_WIDTH);
    private final Button clearButton = new Button();
    private final BooleanProperty clearButtonVisible = new SimpleBooleanProperty();

    public TextInputComponent() {
        createUi();
    }

    public String getLabelText() {
        return label.getText();
    }

    public StringProperty labelTextProperty() {
        return label.textProperty();
    }

    public void setLabelText(String text) {
        label.setText(text);
    }

    public double getLabelWidth() {
        return labelWidth.get();
    }

    public DoubleProperty labelWidthProperty() {
        return labelWidth;
    }

    public void setLabelWidth(double labelWidth) {
        this.labelWidth.set(labelWidth);
    }

    public BooleanProperty clearButtonVisibleProperty() {
        return clearButtonVisible;
    }

    protected HBox getInputBox() {
        return inputBox;
    }

    protected TextField getTextField() {
        return textField;
    }

    protected Label getLabel() {
        return label;
    }

    protected Button getClearButton() {
        return clearButton;
    }

    private void createUi() {
        getStyleClass().add(STYLE_CLASS);

        StackPane textFieldPane = new StackPane(textField, clearButton);
        StackPane.setAlignment(clearButton, Pos.CENTER_RIGHT);

        inputBox.getChildren().add(textFieldPane);
        inputBox.getStyleClass().add(INPUT_BOX_STYLE_CLASS);
        inputBox.setAlignment(Pos.CENTER_RIGHT);

        textFieldPane.setMaxWidth(Double.MAX_VALUE);
        textField.setMaxWidth(Double.MAX_VALUE);
        textField.setContextMenu(new ContextMenu());

        clearButton.setGraphic(Svg.MINUS_TINY.node());
        clearButton.setMinSize(0, 0);
        clearButton.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        clearButton.getStyleClass().setAll(CLEAR_BUTTON_STYLE_CLASS);
        clearButton.setFocusTraversable(false);
        clearButton.visibleProperty().bind(textFieldPane.hoverProperty().and(textField.textProperty().isNotEmpty()).and(clearButtonVisible));

        HBox.setHgrow(textFieldPane, Priority.ALWAYS);
        setValignment(label, VPos.CENTER);

        add(label, 0, 0);
        add(inputBox, 1, 0);

        ColumnConstraints firstColumn = new ColumnConstraints();
        firstColumn.percentWidthProperty().bind(labelWidth);
        ColumnConstraints secondColumn = new ColumnConstraints();
        secondColumn.percentWidthProperty().bind(labelWidth.multiply(-1).add(100));
        getColumnConstraints().addAll(firstColumn, secondColumn);
    }
}