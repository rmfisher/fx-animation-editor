package animator.command;

import animator.model.NodeModel;
import animator.model.SceneModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReorderCommand implements Command {

    private final SceneModel sceneModel;
    private final Type type;
    private final List<NodeModel> nodesToReorder;
    private final Map<NodeModel, Integer> originalIndices = new HashMap<>();

    public static boolean isSelectionAtFront(SceneModel sceneModel) {
        int nodeCount = sceneModel.getNodes().size();
        int selectedNodeCount = (int) sceneModel.getSelectedNodes().count();
        return sceneModel.getSelectedNodes().collect(Collectors.toList()).equals(sceneModel.getNodes().subList(nodeCount - selectedNodeCount, nodeCount));
    }

    public static boolean isSelectionAtBack(SceneModel sceneModel) {
        return sceneModel.getSelectedNodes().collect(Collectors.toList()).equals(sceneModel.getNodes().subList(0, (int) sceneModel.getSelectedNodes().count()));
    }

    public ReorderCommand(SceneModel sceneModel, Type type) {
        this.sceneModel = sceneModel;
        this.type = type;
        this.nodesToReorder = sceneModel.getSelectedNodes().collect(Collectors.toList());
        nodesToReorder.forEach(node -> originalIndices.put(node, sceneModel.getNodes().indexOf(node)));
    }

    @Override
    public void execute() {
        sceneModel.getNodes().removeAll(nodesToReorder);
        if (type == Type.FRONT) {
            sceneModel.getNodes().addAll(nodesToReorder);
        } else {
            sceneModel.getNodes().addAll(0, nodesToReorder);
        }
    }

    @Override
    public void undo() {
        sceneModel.getNodes().removeAll(nodesToReorder);
        nodesToReorder.stream().sorted(Comparator.comparing(originalIndices::get)).forEach(node -> sceneModel.getNodes().add(originalIndices.get(node), node));
    }

    public enum Type {
        FRONT, BACK
    }
}
