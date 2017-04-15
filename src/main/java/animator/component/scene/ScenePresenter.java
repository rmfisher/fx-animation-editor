package animator.component.scene;

import com.google.common.eventbus.EventBus;
import javafx.scene.Node;
import animator.component.scene.selection.LastSelectedNodeTracker;
import animator.component.scene.selection.SelectionClickBehavior;
import animator.event.MenuActionEvent;
import animator.model.GroupModel;
import animator.model.NodeModel;
import animator.model.PlayerModel;
import animator.model.SceneModel;

import javax.inject.Inject;

import static animator.component.util.binding.BindingFunctions.bind;

public class ScenePresenter {

    private final SceneModel model;
    private final PlayerModel playerModel;
    private final SelectionClickBehavior clickBehavior;
    private final EventBus eventBus;

    private SceneComponent view;

    @Inject
    public ScenePresenter(SceneModel model, PlayerModel playerModel, SelectionClickBehavior clickBehavior, EventBus eventBus) {
        this.model = model;
        this.playerModel = playerModel;
        this.clickBehavior = clickBehavior;
        this.eventBus = eventBus;
    }

    void setView(SceneComponent view) {
        this.view = view;
        bindModelAndView();
        addActionHandlers();
        configureSelection();
    }

    private void bindModelAndView() {
        bind(view.getSceneContent().getChildren(), model.getNodes(), this::getNode);
        view.getSceneContent().mouseTransparentProperty().bind(playerModel.playModeProperty());
    }

    private Node getNode(NodeModel nodeModel) {
        Node node = nodeModel.getNode();
        if (nodeModel instanceof GroupModel) {
            GroupModel groupModel = (GroupModel) nodeModel;
            bind(groupModel.getNode().getChildren(), groupModel.getChildren(), this::getNode);
        }
        return node;
    }

    private void addActionHandlers() {
        view.getAddButton().setOnAction(event -> eventBus.post(MenuActionEvent.ADD_ELEMENT));
    }

    private void configureSelection() {
        new LastSelectedNodeTracker(model);
        clickBehavior.addBackgroundHandler(view.getRoot());
    }
}
