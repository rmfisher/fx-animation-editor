package animator.component.scene.panning;

import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import javax.inject.Inject;

public class PanningComponent {

    private final Group content = new Group();
    private final Pane root = new Pane(content);
    private final PanningPresenter presenter;
    private Rectangle clip = new Rectangle();

    @Inject
    public PanningComponent(PanningPresenter presenter) {
        this.presenter = presenter;
        initUi();
        initPresenter(presenter);
    }

    public Region getRoot() {
        return root;
    }

    public ObservableList<Node> getChildren() {
        return content.getChildren();
    }

    public void centerContent() {
        presenter.centerContent();
    }

    public DoubleProperty panXProperty() {
        return content.layoutXProperty();
    }

    public DoubleProperty panYProperty() {
        return content.layoutYProperty();
    }

    Group getContent() {
        return content;
    }

    private void initUi() {
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);
    }

    private void initPresenter(PanningPresenter presenter) {
        presenter.setView(this);
    }
}
