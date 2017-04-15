package animator.component.util.widget;

import javafx.scene.Node;

import java.util.function.Consumer;

public class ActionComboButton<T> extends ComboButton<T> {

    private static final String STYLE_CLASS = "action-combo-button";

    private Consumer<T> actionHandler;

    public ActionComboButton() {
        getStyleClass().add(STYLE_CLASS);
    }

    public void setOnAction(Consumer<T> actionHandler) {
        this.actionHandler = actionHandler;
    }

    @Override
    protected Node createCell(T item) {
        Node cell = super.createCell(item);
        cell.setOnMousePressed(event -> {
            if (actionHandler != null) {
                actionHandler.accept(item);
            }
        });
        return cell;
    }
}
