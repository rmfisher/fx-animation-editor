package animator.command;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import animator.event.TimelineSceneSynchronizer;
import animator.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UngroupCommand implements Command {

    private final Map<KeyFrameModel, Map<AnimatableField, KeyValueModel>> keyValues = new HashMap<>();
    private final GroupModel groupModel;
    private final SceneModel sceneModel;
    private final TimelineModel timelineModel;
    private final List<NodeModel> nodesToUngroup;
    private final TimelineSceneSynchronizer timelineSceneSynchronizer;
    private final Point2D groupPosition;

    public UngroupCommand(GroupModel groupModel, SceneModel sceneModel, TimelineModel timelineModel, TimelineSceneSynchronizer timelineSceneSynchronizer) {
        this.groupModel = groupModel;
        this.sceneModel = sceneModel;
        this.timelineModel = timelineModel;
        this.timelineSceneSynchronizer = timelineSceneSynchronizer;
        this.nodesToUngroup = new ArrayList<>(groupModel.getChildren());
        groupPosition = getGroupPosition();
        recordKeyValuesToRemove();
    }

    @Override
    public void execute() {
        groupModel.setSelected(false);

        groupModel.getChildren().clear();
        sceneModel.getNodes().remove(groupModel);
        nodesToUngroup.forEach(n -> timelineModel.getKeyFrames().forEach(f -> moveChildByGroupPosition(n, f, 1)));
        nodesToUngroup.forEach(n -> n.setSelected(true));
        nodesToUngroup.forEach(n -> n.setGroup(null));
        sceneModel.getNodes().addAll(nodesToUngroup);

        timelineSceneSynchronizer.onNodeRemoved(groupModel);
        timelineModel.getKeyFrames().forEach(k -> k.getKeyValues().remove(groupModel));
    }

    @Override
    public void undo() {
        sceneModel.getNodes().removeAll(nodesToUngroup);
        nodesToUngroup.forEach(n -> n.setGroup(groupModel));
        nodesToUngroup.forEach(n -> n.setSelected(false));
        nodesToUngroup.forEach(n -> timelineModel.getKeyFrames().forEach(f -> moveChildByGroupPosition(n, f, -1)));
        sceneModel.getNodes().add(groupModel);
        groupModel.getChildren().addAll(nodesToUngroup);

        timelineModel.getKeyFrames().forEach(k -> k.getKeyValues().put(groupModel, keyValues.get(k)));
        timelineSceneSynchronizer.onNodeAdded(groupModel);

        groupModel.setSelected(true);
    }

    private Point2D getGroupPosition() {
        return new Point2D(groupModel.getNode().getLayoutX(), groupModel.getNode().getLayoutY());
    }

    private void moveChildByGroupPosition(NodeModel childNode, KeyFrameModel keyFrame, int factor) {
        ObjectProperty<Object> x = keyFrame.getKeyValues().get(childNode).get(AnimatableField.LAYOUT_X).valueProperty();
        ObjectProperty<Object> y = keyFrame.getKeyValues().get(childNode).get(AnimatableField.LAYOUT_Y).valueProperty();
        if (x.get() != null) {
            x.set(((Double) x.get()) + factor * groupPosition.getX());
        }
        if (y.get() != null) {
            y.set(((Double) y.get()) + factor * groupPosition.getY());
        }
    }

    private void recordKeyValuesToRemove() {
        timelineModel.getKeyFrames().forEach(k -> keyValues.put(k, k.getKeyValues().get(groupModel)));
    }
}
