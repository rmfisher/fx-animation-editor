package animator.command;

import animator.model.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NodeDragCommand implements Command {

    private final Map<KeyValueModel, Object> start;
    private final Map<KeyValueModel, Object> end;
    private final TimelineModel timelineModel;
    private final KeyFrameModel keyFrame;
    private final List<NodeModel> nodes;

    public NodeDragCommand(Map<KeyValueModel, Object> start, Map<KeyValueModel, Object> end, TimelineModel timelineModel, SceneModel sceneModel) {
        this.start = start;
        this.end = end;
        this.timelineModel = timelineModel;
        this.keyFrame = timelineModel.getSelectedKeyFrame();
        this.nodes = sceneModel.getSelectedNodes().collect(Collectors.toList());
    }

    @Override
    public void execute() {
        timelineModel.setSelectedKeyFrame(keyFrame);
        nodes.forEach(n -> n.setSelected(true));
        end.forEach(KeyValueModel::setValue);
    }

    @Override
    public void undo() {
        timelineModel.setSelectedKeyFrame(keyFrame);
        nodes.forEach(n -> n.setSelected(true));
        start.forEach(KeyValueModel::setValue);
    }
}
