package animator.component;

import com.google.common.eventbus.EventBus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import animator.component.util.binding.ChainedBindingFunctions;
import animator.event.MenuActionEvent;
import animator.model.*;

import javax.inject.Inject;

public class AnimationEditorPresenter {

    private final PropertyModel propertyModel;
    private final SceneModel sceneModel;
    private TimelineModel timelineModel;
    private final EventBus eventBus;

    private AnimationEditorComponent view;

    @Inject
    public AnimationEditorPresenter(PropertyModel propertyModel, SceneModel sceneModel, TimelineModel timelineModel, EventBus eventBus) {
        this.propertyModel = propertyModel;
        this.sceneModel = sceneModel;
        this.timelineModel = timelineModel;
        this.eventBus = eventBus;
    }

    void setView(AnimationEditorComponent view) {
        this.view = view;
        initBindings();
        initHandlers();
    }

    private void initBindings() {
        propertyModel.nodeTypeProperty().bind(Bindings.createObjectBinding(this::getNodeType, sceneModel.lastSelectedNodeProperty()));
        propertyModel.setSnapToGrid(sceneModel.isSnapToGrid());
        propertyModel.nonZeroKeyFrameProperty().bind(nonZeroKeyFrameSelected());
        sceneModel.snapToGridProperty().bindBidirectional(propertyModel.snapToGridProperty());
    }

    private BooleanBinding nonZeroKeyFrameSelected() {
        return ChainedBindingFunctions.from(timelineModel.selectedKeyFrameProperty()).mapToDouble(KeyFrameModel::getTime).greaterThan(0);
    }

    private NodeType getNodeType() {
        NodeModel n = sceneModel.getLastSelectedNode();
        return n instanceof RectangleModel ? NodeType.RECTANGLE : n instanceof GroupModel ? NodeType.GROUP : null;
    }

    private void initHandlers() {
        view.getRoot().addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.BACK_SPACE) {
            eventBus.post(MenuActionEvent.DELETE_SELECTION);
        }
    }
}
