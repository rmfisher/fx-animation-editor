package animator.component.scene.selection;

import javafx.beans.property.ReadOnlyListProperty;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import animator.component.scene.panning.PanningComponent;
import animator.model.NodeModel;
import animator.model.PlayerModel;
import animator.model.SceneModel;

import javax.inject.Inject;

public class SelectionBoxComponent {

    private static final String STYLE_CLASS = "selection-box";

    private final Rectangle root = new Rectangle();
    private final ReadOnlyListProperty<NodeModel> nodes;
    private final PlayerModel playerModel;
    private final PanningComponent panningComponent;

    private boolean dragActive;
    private double xAtPress;
    private double yAtPress;

    @Inject
    public SelectionBoxComponent(SceneModel sceneModel, PlayerModel playerModel, PanningComponent panningComponent) {
        this.nodes = sceneModel.nodesProperty();
        this.playerModel = playerModel;
        this.panningComponent = panningComponent;
        initUi();
        addHandlers();
    }

    public Rectangle getRoot() {
        return root;
    }

    private void initUi() {
        root.getStyleClass().add(STYLE_CLASS);
        root.setVisible(false);
    }

    private void addHandlers() {
        panningComponent.getRoot().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onBackgroundPressed);
        panningComponent.getRoot().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onBackgroundDragged);
        panningComponent.getRoot().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onBackgroundReleased);
    }

    private void onBackgroundPressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && !playerModel.isPlayMode()) {
            xAtPress = event.getX() - panningComponent.panXProperty().get();
            yAtPress = event.getY() - panningComponent.panYProperty().get();
            dragActive = true;
        }
    }

    private void onBackgroundDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && dragActive) {
            double x = event.getX() - panningComponent.panXProperty().get();
            double y = event.getY() - panningComponent.panYProperty().get();

            double minX = Math.min(x, xAtPress);
            double minY = Math.min(y, yAtPress);
            double maxX = Math.max(x, xAtPress);
            double maxY = Math.max(y, yAtPress);

            root.setVisible(true);
            root.setLayoutX(minX);
            root.setLayoutY(minY);
            root.setWidth(maxX - minX);
            root.setHeight(maxY - minY);
        }
    }

    private void onBackgroundReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            root.setVisible(false);
            dragActive = false;
            if (!event.isStillSincePress()) {
                selectInBounds(root.getBoundsInParent());
            }
            root.setWidth(0);
            root.setHeight(0);
        }
    }

    private void selectInBounds(Bounds boundsInParent) {
        nodes.stream().filter(e -> boundsInParent.contains(e.getNode().getBoundsInParent())).forEach(e -> e.setSelected(true));
    }
}
