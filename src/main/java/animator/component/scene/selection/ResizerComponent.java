package animator.component.scene.selection;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import animator.component.util.widget.EightBoxes;
import animator.model.GroupModel;
import animator.model.NodeModel;
import animator.model.SceneModel;
import animator.util.MathFunctions;

import javax.inject.Inject;
import java.util.Objects;

public class ResizerComponent extends SelectionListener {

    private static final String STYLE_CLASS = "resizer-group";
    private static final String RECTANGLE_STYLE_CLASS = "rectangle";
    private static final double RESIZER_OFFSET = 6;

    private final EightBoxes resizers = new EightBoxes();
    private final Rectangle rectangle = new Rectangle();
    private final Group root = new Group(rectangle, resizers);
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private final ChangeListener<Bounds> boundsChangeListener = (v, o, n) -> layout();
    private final SceneModel sceneModel;

    @Inject
    public ResizerComponent(SceneModel sceneModel) {
        super(sceneModel);
        this.sceneModel = sceneModel;
        initUi();
        initBoundsListeners();
    }

    public Node getRoot() {
        return root;
    }

    EightBoxes getResizers() {
        return resizers;
    }

    double getX() {
        return x.get();
    }

    DoubleProperty xProperty() {
        return x;
    }

    double getY() {
        return y.get();
    }

    DoubleProperty yProperty() {
        return y;
    }

    double getWidth() {
        return width.get();
    }

    DoubleProperty widthProperty() {
        return width;
    }

    double getHeight() {
        return height.get();
    }

    DoubleProperty heightProperty() {
        return height;
    }

    private void initUi() {
        root.getStyleClass().add(STYLE_CLASS);
        root.setManaged(false);

        rectangle.getStyleClass().add(RECTANGLE_STYLE_CLASS);
        rectangle.setMouseTransparent(true);
        rectangle.layoutXProperty().bind(x.subtract(RESIZER_OFFSET));
        rectangle.layoutYProperty().bind(y.subtract(RESIZER_OFFSET));
        rectangle.widthProperty().bind(width.add(2 * RESIZER_OFFSET));
        rectangle.heightProperty().bind(height.add(2 * RESIZER_OFFSET));

        resizers.layoutXProperty().bind(rectangle.layoutXProperty());
        resizers.layoutYProperty().bind(rectangle.layoutYProperty());
        resizers.widthProperty().bind(rectangle.widthProperty());
        resizers.heightProperty().bind(rectangle.heightProperty());
        resizers.getChildrenUnmodifiable().forEach(c -> c.setPickOnBounds(true));

        resizers.getTopLeft().setCursor(Cursor.NW_RESIZE);
        resizers.getTop().setCursor(Cursor.N_RESIZE);
        resizers.getTopRight().setCursor(Cursor.NE_RESIZE);
        resizers.getRight().setCursor(Cursor.E_RESIZE);
        resizers.getBottomRight().setCursor(Cursor.SE_RESIZE);
        resizers.getBottom().setCursor(Cursor.S_RESIZE);
        resizers.getBottomLeft().setCursor(Cursor.SW_RESIZE);
        resizers.getLeft().setCursor(Cursor.W_RESIZE);
    }

    private void initBoundsListeners() {
        sceneModel.getSelectedNodes().forEach(n -> n.getNode().boundsInParentProperty().addListener(boundsChangeListener));
        layout();
    }

    @Override
    protected void onSelectionChanged(NodeModel nodeModel) {
        if (nodeModel.isSelected()) {
            nodeModel.getNode().boundsInParentProperty().addListener(boundsChangeListener);
        } else {
            nodeModel.getNode().boundsInParentProperty().removeListener(boundsChangeListener);
        }
        layout();
    }

    private void layout() {
        root.setVisible(sceneModel.getSelectedNodes().count() > 0);
        sceneModel.getSelectedNodes().map(n -> n.getNode().getBoundsInParent()).filter(Objects::nonNull).reduce(MathFunctions::union).ifPresent(this::sizeTo);
        resizers.setMouseTransparent(sceneModel.getSelectedNodes().allMatch(GroupModel.class::isInstance));
    }

    private void sizeTo(Bounds bounds) {
        x.set(Math.round(bounds.getMinX()));
        y.set(Math.round(bounds.getMinY()));
        width.set(Math.round(bounds.getWidth()));
        height.set(Math.round(bounds.getHeight()));
    }
}
