package animator.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.geometry.Point2D;
import animator.command.CommandStack;
import animator.command.NodeDragCommand;
import animator.model.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static animator.event.SelectionDragEvent.Type.*;
import static animator.model.AnimatableField.*;
import static animator.util.MapFunctions.filterByKeyAndForEach;

public class SelectionDragHandler {

    private final CommandStack commandStack;
    private final TimelineModel timelineModel;
    private final SceneModel sceneModel;

    private Map<KeyValueModel, Object> startValues;

    @Inject
    public SelectionDragHandler(CommandStack commandStack, TimelineModel timelineModel, SceneModel sceneModel, EventBus eventBus) {
        this.commandStack = commandStack;
        this.timelineModel = timelineModel;
        this.sceneModel = sceneModel;
        subscribeToEvents(eventBus);
    }

    @Subscribe
    public void onSelectionDragged(SelectionDragEvent event) {
        if (timelineModel.getSelectedKeyFrame() == null) {
            return;
        }
        if (event.getType() == START) {
            startValues = new HashMap<>();
            filterByKeyAndForEach(getKeyValues(), NodeModel::isSelected, this::storeStartKeyValues);
        } else if (event.getType() == DRAG) {
            event.getNewPositions().keySet().forEach(node -> moveNode(node, event));
        } else if (event.getType() == FINISHED) {
            Map<KeyValueModel, Object> endValues = getEndValues(event);
            if (startValues != null && anyNodeMoved(endValues)) {
                commandStack.append(new NodeDragCommand(startValues, endValues, timelineModel, sceneModel));
            }
        }
    }

    private Map<NodeModel, Map<AnimatableField, KeyValueModel>> getKeyValues() {
        return timelineModel.getSelectedKeyFrame().getKeyValues();
    }

    private void storeStartKeyValues(Map<AnimatableField, KeyValueModel> keyValues) {
        filterByKeyAndForEach(keyValues, this::isLayoutField, v -> startValues.put(v, v.getValue()));
    }

    private boolean isLayoutField(AnimatableField field) {
        return field == LAYOUT_X || field == LAYOUT_Y;
    }

    private void moveNode(NodeModel node, SelectionDragEvent event) {
        getKeyValues().get(node).get(LAYOUT_X).setValue(event.getNewPositions().get(node).getX());
        getKeyValues().get(node).get(LAYOUT_Y).setValue(event.getNewPositions().get(node).getY());
    }

    public Map<KeyValueModel, Object> getEndValues(SelectionDragEvent event) {
        Map<KeyValueModel, Object> endValues = new HashMap<>();
        getKeyValues().forEach((node, values) -> {
            if (event.getNewPositions().containsKey(node)) {
                addLayoutKeyValues(endValues, values, event.getNewPositions().get(node));
            }
        });
        return endValues;
    }

    private void addLayoutKeyValues(Map<KeyValueModel, Object> endValues, Map<AnimatableField, KeyValueModel> keyValues, Point2D point) {
        endValues.put(keyValues.get(LAYOUT_X), point.getX());
        endValues.put(keyValues.get(LAYOUT_Y), point.getY());
    }

    private boolean anyNodeMoved(Map<KeyValueModel, Object> endValues) {
        return startValues.entrySet().stream().anyMatch(e -> !Objects.equals(e.getValue(), endValues.get(e.getKey())));
    }

    private void subscribeToEvents(EventBus eventBus) {
        eventBus.register(this);
    }
}
