package animator.component.property.control;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class RotateComponent extends DoubleInputComponent {

    private static final String STYLE_CLASS = "rotate-component";
    private static final String ROTATER_BOX_STYLE_CLASS = "rotator-box";

    private final RotatorDialComponent rotatorDialComponent = new RotatorDialComponent();
    private final ObjectProperty<Double> rotatorDialValueAsObject = rotatorDialComponent.valueProperty().asObject();

    public RotateComponent() {
        createUi();
    }

    public void setOnChangeStarted(Runnable onChangeStarted) {
        rotatorDialComponent.setOnChangeStarted(onChangeStarted);
    }

    public void setOnChange(Runnable onChange) {
        rotatorDialComponent.setOnChange(onChange);
    }

    public void setOnChangeFinished(Runnable onChangeFinished) {
        rotatorDialComponent.setOnChangeFinished(onChangeFinished);
    }

    private void createUi() {
        getStyleClass().add(STYLE_CLASS);

        HBox innerBox = new HBox(getInputBox().getChildren().remove(0), rotatorDialComponent.getRoot());
        innerBox.getStyleClass().add(ROTATER_BOX_STYLE_CLASS);
        innerBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(innerBox, Priority.ALWAYS);
        getInputBox().getChildren().add(0, innerBox);

        rotatorDialValueAsObject.bindBidirectional(valueProperty());
    }
}
