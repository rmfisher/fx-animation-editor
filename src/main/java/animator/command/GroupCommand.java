package animator.command;

import animator.event.TimelineSceneSynchronizer;
import animator.model.GroupModel;
import animator.model.NodeModel;
import animator.model.SceneModel;
import animator.model.TimelineModel;

import java.util.List;

public class GroupCommand extends AddNodeCommand {

    private final SceneModel sceneModel;
    private final GroupModel groupModel;
    private final List<NodeModel> nodesToGroup;

    public GroupCommand(SceneModel sceneModel, TimelineModel timelineModel, TimelineSceneSynchronizer timelineSceneSynchronizer, GroupModel groupModel,
                        List<NodeModel> nodesToGroup) {

        super(groupModel, timelineModel, timelineSceneSynchronizer);
        this.sceneModel = sceneModel;
        this.groupModel = groupModel;
        this.nodesToGroup = nodesToGroup;
    }

    @Override
    public void execute() {
        addKeyValues();
        nodesToGroup.forEach(n -> n.setSelected(false));
        sceneModel.getNodes().removeAll(nodesToGroup);
        groupModel.getChildren().addAll(nodesToGroup);
        nodesToGroup.forEach(nodeModel -> nodeModel.setGroup(groupModel));
        sceneModel.getNodes().add(groupModel);
        groupModel.setSelected(true);
    }

    @Override
    public void undo() {
        groupModel.setSelected(false);
        groupModel.getChildren().clear();
        sceneModel.getNodes().remove(groupModel);
        nodesToGroup.forEach(child -> child.setSelected(true));
        nodesToGroup.forEach(child -> child.setGroup(null));
        sceneModel.getNodes().addAll(nodesToGroup);
        removeKeyValues();
    }
}
