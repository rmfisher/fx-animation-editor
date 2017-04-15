package animator.component.property.control;

import javafx.scene.Node;
import javafx.scene.control.Label;
import animator.component.util.binding.ChainedBindingFunctions;
import animator.component.util.widget.SelectionComboButton;
import animator.model.interpolator.InterpolatorModel;

import java.util.function.Supplier;

public class InterpolatorButtonComponent extends SelectionComboButton<InterpolatorModel> {

    private static final String STYLE_CLASS = "interpolator-button";

    public InterpolatorButtonComponent() {
        getStyleClass().add(STYLE_CLASS);
        setCellFactory(this::createInterpolatorCell);
        graphicProperty().bind(ChainedBindingFunctions.from(selectionProperty()).select(InterpolatorModel::iconSupplierProperty).mapToObject(Supplier::get));
    }

    private Node createInterpolatorCell(InterpolatorModel item) {
        Label label = new Label();
        label.textProperty().bind(item.nameProperty());
        label.graphicProperty().bind(ChainedBindingFunctions.from(item.iconSupplierProperty()).mapToObject(Supplier::get));
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }
}
