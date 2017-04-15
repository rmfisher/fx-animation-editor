package animator.event;

import animator.command.CommandStack;
import animator.command.DeleteNodeCommand;
import animator.command.PasteCommand;
import animator.model.*;
import animator.util.ModelFunctions;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.stream.Collectors.toList;

public class Clipboard {

    private static final double PASTE_OFFSET = 20;

    private final TimelineModel timelineModel;
    private final SceneModel sceneModel;
    private final TimelineSceneSynchronizer timelineSceneSynchronizer;
    private final CommandStack commandStack;

    private List<NodeModel> copiedNodes;
    private Map<KeyFrameModel, Map<NodeModel, Map<AnimatableField, KeyValueModel>>> copiedKeyValues;
    private Map<KeyFrameModel, Map<NodeModel, Map<AnimatableField, KeyValueModel>>> keyValuesToPaste;
    private KeyFrameModel selectedKeyFrameAtCopy;

    @Inject
    public Clipboard(TimelineModel timelineModel, SceneModel sceneModel, TimelineSceneSynchronizer timelineSceneSynchronizer, CommandStack commandStack) {
        this.timelineModel = timelineModel;
        this.sceneModel = sceneModel;
        this.timelineSceneSynchronizer = timelineSceneSynchronizer;
        this.commandStack = commandStack;
    }

    public void cut() {
        if (sceneModel.getSelectedNodes().count() > 0) {
            copy();
            commandStack.appendAndExecute(new DeleteNodeCommand(sceneModel, timelineModel, timelineSceneSynchronizer));
        }
    }

    public void copy() {
        if (sceneModel.getSelectedNodes().count() > 0) {
            copiedKeyValues = new HashMap<>();
            copiedNodes = copyNodes();
            selectedKeyFrameAtCopy = timelineModel.getSelectedKeyFrame();
        }
    }

    public void paste() {
        if (copiedNodes != null) {
            keyValuesToPaste = new HashMap<>();
            List<NodeModel> nodesToPaste = prepareNodesForPaste();
            translateAll();
            commandStack.appendAndExecute(new PasteCommand(sceneModel, timelineModel, timelineSceneSynchronizer, nodesToPaste, keyValuesToPaste));
            keyValuesToPaste = null;
        }
    }

    private List<NodeModel> copyNodes() {
        return sceneModel.getNodes().stream().filter(NodeModel::isSelected).map(n -> copyNodeAndKeyValues(n, null)).collect(toList());
    }

    private NodeModel copyNodeAndKeyValues(NodeModel input, GroupModel copiedGroup) {
        return copy(input, copiedGroup, this::copyKeyValues);
    }

    private NodeModel copy(NodeModel input, GroupModel copiedGroup, BiConsumer<NodeModel, NodeModel> consumer) {
        NodeModel copy;
        if (input instanceof RectangleModel) {
            copy = new RectangleModel();
        } else if (input instanceof GroupModel) {
            GroupModel groupModel = new GroupModel();
            groupModel.getChildren().setAll(((GroupModel) input).getChildren().stream().map(n -> copy(n, groupModel, consumer)).collect(toList()));
            copy = groupModel;
        } else {
            throw new UnsupportedOperationException();
        }
        copy.setGroup(copiedGroup);
        consumer.accept(input, copy);
        return copy;
    }

    private void copyKeyValues(NodeModel input, NodeModel copy) {
        timelineModel.getKeyFrames().forEach(k -> copyKeyValues(k, input, copy));
    }

    private void copyKeyValues(KeyFrameModel keyFrame, NodeModel input, NodeModel copy) {
        if (!copiedKeyValues.containsKey(keyFrame)) {
            copiedKeyValues.put(keyFrame, new HashMap<>());
        }
        copiedKeyValues.get(keyFrame).put(copy, ModelFunctions.deepCopy(keyFrame.getKeyValues().get(input)));
    }

    private List<NodeModel> prepareNodesForPaste() {
        return copiedNodes.stream().map(n -> prepareNodeForPaste(n, null)).collect(toList());
    }

    private NodeModel prepareNodeForPaste(NodeModel input, GroupModel copiedGroup) {
        return copy(input, copiedGroup, this::prepareKeyValuesForPaste);
    }

    private void prepareKeyValuesForPaste(NodeModel input, NodeModel copy) {
        timelineModel.getKeyFrames().forEach(k -> prepareKeyValuesForPaste(k, input, copy));
    }

    private void prepareKeyValuesForPaste(KeyFrameModel keyFrame, NodeModel input, NodeModel copy) {
        if (!keyValuesToPaste.containsKey(keyFrame)) {
            keyValuesToPaste.put(keyFrame, new HashMap<>());
        }
        keyValuesToPaste.get(keyFrame).put(copy, ModelFunctions.deepCopy(getKeyValuesToPaste(keyFrame).get(input)));
    }

    private Map<NodeModel, Map<AnimatableField, KeyValueModel>> getKeyValuesToPaste(KeyFrameModel keyFrame) {
        if (copiedKeyValues.containsKey(keyFrame)) {
            return copiedKeyValues.get(keyFrame);
        } else {
            return copiedKeyValues.get(selectedKeyFrameAtCopy);
        }
    }

    private void translateAll() {
        keyValuesToPaste.values().forEach(f -> f.values().forEach(this::translate));
    }

    private void translate(Map<AnimatableField, KeyValueModel> keyValues) {
        KeyValueModel layoutX = keyValues.get(AnimatableField.LAYOUT_X);
        KeyValueModel layoutY = keyValues.get(AnimatableField.LAYOUT_Y);
        if (layoutX.getValue() != null) {
            layoutX.setValue((double) layoutX.getValue() + PASTE_OFFSET);
        }
        if (layoutY.getValue() != null) {
            layoutY.setValue((double) layoutY.getValue() + PASTE_OFFSET);
        }
    }
}
