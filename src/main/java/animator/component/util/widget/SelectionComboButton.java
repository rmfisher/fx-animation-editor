package animator.component.util.widget;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

public class SelectionComboButton<T> extends ComboButton<T> {

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    private final ObjectProperty<T> selection = new SimpleObjectProperty<>();
    private Runnable onItemSelected;

    public T getSelection() {
        return selection.get();
    }

    public ObjectProperty<T> selectionProperty() {
        return selection;
    }

    public void setSelection(T selection) {
        this.selection.set(selection);
    }

    public void setOnItemSelected(Runnable onItemSelected) {
        this.onItemSelected = onItemSelected;
    }

    @Override
    protected Node createCell(T item) {
        Node cell = super.createCell(item);
        cell.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onCellPressed(item));
        selection.addListener((v, o, n) -> cell.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, item != null && item.equals(n)));
        return cell;
    }

    private void onCellPressed(T item) {
        if (!Objects.equals(item, getSelection())) {
            setSelection(item);
            if (onItemSelected != null) {
                onItemSelected.run();
            }
        }
    }
}
