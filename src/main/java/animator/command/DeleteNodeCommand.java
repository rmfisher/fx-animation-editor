package animator.command;

import animator.event.TimelineSceneSynchronizer;
import animator.model.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DeleteNodeCommand implements Command {

    private final SceneModel sceneModel;
    private final TimelineModel timelineModel;
    private final TimelineSceneSynchronizer timelineSceneSynchronizer;
    private final List<NodeModel> nodesToDelete;
    private final Map<NodeModel, Integer> originalIndices = new HashMap<>();
    private final Map<KeyFrameModel, Map<NodeModel, Map<AnimatableField, KeyValueModel>>> keyValuesOfDeletedNodes = new HashMap<>();

    public DeleteNodeCommand(SceneModel sceneModel, TimelineModel timelineModel, TimelineSceneSynchronizer timelineSceneSynchronizer) {
        this.sceneModel = sceneModel;
        this.timelineModel = timelineModel;
        this.timelineSceneSynchronizer = timelineSceneSynchronizer;
        nodesToDelete = sceneModel.getSelectedNodes().collect(Collectors.toList());
        nodesToDelete.forEach(node -> originalIndices.put(node, sceneModel.getNodes().indexOf(node)));
        recordKeyValuesOfDeletedNodes();
    }

    @Override
    public void execute() {
        sceneModel.getSelectedNodes().forEach(n -> n.setSelected(false));
        nodesToDelete.forEach(this::removeKeyValuesFromTimeline);
        sceneModel.getNodes().removeAll(nodesToDelete);
    }

    @Override
    public void undo() {
        nodesToDelete.stream().sorted(Comparator.comparing(originalIndices::get)).forEach(node -> sceneModel.getNodes().add(originalIndices.get(node), node));
        nodesToDelete.forEach(this::addKeyValuesToTimeline);
        nodesToDelete.forEach(n -> n.setSelected(true));
    }

    private void recordKeyValuesOfDeletedNodes() {
        timelineModel.getKeyFrames().forEach(k -> nodesToDelete.forEach(n -> recordKeyValuesOfDeletedNode(k, n)));
    }

    private void recordKeyValuesOfDeletedNode(KeyFrameModel keyFrame, NodeModel node) {
        Map<NodeModel, Map<AnimatableField, KeyValueModel>> keyValues = keyValuesOfDeletedNodes.computeIfAbsent(keyFrame, k -> new HashMap<>());
        keyValues.put(node, keyFrame.getKeyValues().get(node));
        if (node instanceof GroupModel) {
            ((GroupModel) node).getChildren().forEach(n -> recordKeyValuesOfDeletedNode(keyFrame, n));
        }
    }

    private void removeKeyValuesFromTimeline(NodeModel node) {
        timelineModel.getKeyFrames().forEach(f -> f.getKeyValues().remove(node));
        timelineSceneSynchronizer.onNodeRemoved(node);
        if (node instanceof GroupModel) {
            ((GroupModel) node).getChildren().forEach(this::removeKeyValuesFromTimeline);
        }
    }

    private void addKeyValuesToTimeline(NodeModel node) {
        timelineModel.getKeyFrames().forEach(f -> f.getKeyValues().put(node, keyValuesOfDeletedNodes.get(f).get(node)));
        timelineSceneSynchronizer.onNodeAdded(node);
        if (node instanceof GroupModel) {
            ((GroupModel) node).getChildren().forEach(this::addKeyValuesToTimeline);
        }
    }
}
