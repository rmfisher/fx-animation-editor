package animator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public abstract class NodeModel {

    private final BooleanProperty selected = new SimpleBooleanProperty();
    private final ObjectProperty<GroupModel> group = new SimpleObjectProperty<>();

    public abstract Node getNode();

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public GroupModel getGroup() {
        return group.get();
    }

    public ObjectProperty<GroupModel> groupProperty() {
        return group;
    }

    public void setGroup(GroupModel group) {
        this.group.set(group);
    }
}
