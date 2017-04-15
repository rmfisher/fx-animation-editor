package animator.component.scene.selection;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import animator.model.NodeModel;
import animator.model.SceneModel;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class SelectionClickBehavior extends NodeChangeListener {

    private final SceneModel sceneModel;
    private final Map<NodeModel, EventHandler<MouseEvent>> pressedHandlers = new HashMap<>();
    private final Map<NodeModel, EventHandler<MouseEvent>> clickedHandlers = new HashMap<>();
    private final EventHandler<MouseEvent> eventConsumer = this::consumeIfPrimary;
    private boolean lastElementSelectedAtShortcutAndPress;

    @Inject
    public SelectionClickBehavior(SceneModel sceneModel) {
        super(sceneModel);
        this.sceneModel = sceneModel;
    }

    public void addBackgroundHandler(Node background) {
        background.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onBackgroundPressed);
    }

    @Override
    protected void onNodeAdded(NodeModel nodeModel) {
        EventHandler<MouseEvent> pressedHandler =  event -> onPressed(nodeModel, event);
        EventHandler<MouseEvent> clickedHandler =  event -> onClicked(nodeModel, event);
        nodeModel.getNode().addEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
        nodeModel.getNode().addEventFilter(MouseEvent.MOUSE_CLICKED, clickedHandler);
        nodeModel.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED, eventConsumer);
        nodeModel.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, eventConsumer);
        pressedHandlers.put(nodeModel, pressedHandler);
        clickedHandlers.put(nodeModel, clickedHandler);

    }

    @Override
    protected void onNodeRemoved(NodeModel nodeModel) {
        nodeModel.getNode().removeEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandlers.get(nodeModel));
        nodeModel.getNode().removeEventFilter(MouseEvent.MOUSE_CLICKED, clickedHandlers.get(nodeModel));
        nodeModel.getNode().removeEventHandler(MouseEvent.MOUSE_PRESSED, eventConsumer);
        nodeModel.getNode().removeEventHandler(MouseEvent.MOUSE_CLICKED, eventConsumer);
        pressedHandlers.remove(nodeModel);
        clickedHandlers.remove(nodeModel);
    }

    private void onBackgroundPressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && !event.isShortcutDown()) {
            sceneModel.getSelectedNodes().forEach(n -> n.setSelected(false));
        }
    }

    private void onPressed(NodeModel nodeModel, MouseEvent event) {
        lastElementSelectedAtShortcutAndPress = false;
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.isShortcutDown()) {
                if (nodeModel.isSelected()) {
                    lastElementSelectedAtShortcutAndPress = true;
                } else {
                    nodeModel.setSelected(true);
                }
            } else {
                if (!nodeModel.isSelected()) {
                    sceneModel.getSelectedNodes().forEach(n -> n.setSelected(false));
                    nodeModel.setSelected(true);
                }
                sceneModel.setLastSelectedNode(nodeModel);
            }
        }
    }

    private void onClicked(NodeModel nodeModel, MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.isShortcutDown() && event.isStillSincePress() && lastElementSelectedAtShortcutAndPress) {
                nodeModel.setSelected(false);
            }
        }
    }

    private void consumeIfPrimary(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            event.consume();
        }
    }
}
