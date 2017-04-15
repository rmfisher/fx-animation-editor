package animator.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;

public class GroupModel extends NodeModel {

    private final Group group = new Group();
    private final ListProperty<NodeModel> children = new SimpleListProperty<>(FXCollections.observableArrayList());

    @Override
    public Group getNode() {
        return group;
    }

    public ObservableList<NodeModel> getChildren() {
        return children.get();
    }

    public ReadOnlyListProperty<NodeModel> childrenProperty() {
        return children;
    }
}
