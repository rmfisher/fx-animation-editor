package animator.model.interpolator;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InterpolatorListModel {

    private final ListProperty<InterpolatorModel> interpolators = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<InterpolatorModel> selection = new SimpleObjectProperty<>();

    public ObservableList<InterpolatorModel> getInterpolators() {
        return interpolators.get();
    }

    public ReadOnlyListProperty<InterpolatorModel> interpolatorsProperty() {
        return interpolators;
    }

    public InterpolatorModel getSelection() {
        return selection.get();
    }

    public ObjectProperty<InterpolatorModel> selectionProperty() {
        return selection;
    }

    public void setSelection(InterpolatorModel selection) {
        this.selection.set(selection);
    }
}
