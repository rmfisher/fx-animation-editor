package animator.command;

import animator.event.TimelineSceneSynchronizer;
import animator.model.*;

import java.util.List;
import java.util.Map;

public class PasteCommand implements Command {

    private final SceneModel sceneModel;
    private final TimelineModel timelineModel;
    private final TimelineSceneSynchronizer timelineSceneSynchronizer;
    private final List<NodeModel> nodes;
    private final Map<KeyFrameModel, Map<NodeModel, Map<AnimatableField, KeyValueModel>>> keyValues;

    public PasteCommand(SceneModel sceneModel, TimelineModel timelineModel, TimelineSceneSynchronizer timelineSceneSynchronizer, List<NodeModel> nodes,
                        Map<KeyFrameModel, Map<NodeModel, Map<AnimatableField, KeyValueModel>>> keyValues) {

        this.sceneModel = sceneModel;
        this.timelineModel = timelineModel;
        this.timelineSceneSynchronizer = timelineSceneSynchronizer;
        this.nodes = nodes;
        this.keyValues = keyValues;
    }

    @Override
    public void execute() {
        sceneModel.getNodes().forEach(n -> n.setSelected(false));
        sceneModel.getNodes().addAll(nodes);
        timelineModel.getKeyFrames().forEach(k -> k.getKeyValues().putAll(keyValues.get(k)));
        keyValues.forEach((f, k) -> k.keySet().forEach(timelineSceneSynchronizer::onNodeAdded));
        nodes.forEach(n -> n.setSelected(true));
    }

    @Override
    public void undo() {
        nodes.forEach(n -> n.setSelected(false));
        keyValues.forEach((f, k) -> k.keySet().forEach(timelineSceneSynchronizer::onNodeRemoved));
        sceneModel.getNodes().removeAll(nodes);
        keyValues.forEach((f, k) -> k.keySet().forEach(n -> f.getKeyValues().remove(n)));
    }
}
