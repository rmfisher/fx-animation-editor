package animator.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.geometry.Rectangle2D;
import animator.command.CommandStack;
import animator.command.NodeDragCommand;
import animator.model.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static animator.event.SelectionResizeEvent.Type.*;
import static animator.model.AnimatableField.*;
import static animator.util.MapFunctions.filterByKeyAndForEach;

public class SelectionResizeHandler {

    private final CommandStack commandStack;
    private final TimelineModel timelineModel;
    private final SceneModel sceneModel;

    private Map<KeyValueModel, Object> startValues;

    @Inject
    public SelectionResizeHandler(CommandStack commandStack, TimelineModel timelineModel, SceneModel sceneModel, EventBus eventBus) {
        this.commandStack = commandStack;
        this.timelineModel = timelineModel;
        this.sceneModel = sceneModel;
        subscribeToEvents(eventBus);
    }

    @Subscribe
    public void onSelectionResized(SelectionResizeEvent event) {
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
        return field == LAYOUT_X || field == LAYOUT_Y || field == WIDTH || field == HEIGHT;
    }

    private void moveNode(NodeModel node, SelectionResizeEvent event) {
        getKeyValues().get(node).get(LAYOUT_X).setValue(event.getNewPositions().get(node).getMinX());
        getKeyValues().get(node).get(LAYOUT_Y).setValue(event.getNewPositions().get(node).getMinY());
        getKeyValues().get(node).get(WIDTH).setValue(event.getNewPositions().get(node).getWidth());
        getKeyValues().get(node).get(HEIGHT).setValue(event.getNewPositions().get(node).getHeight());
    }

    public Map<KeyValueModel, Object> getEndValues(SelectionResizeEvent event) {
        Map<KeyValueModel, Object> endValues = new HashMap<>();
        getKeyValues().forEach((node, values) -> {
            if (event.getNewPositions().containsKey(node)) {
                addLayoutKeyValues(endValues, values, event.getNewPositions().get(node));
            }
        });
        return endValues;
    }

    private void addLayoutKeyValues(Map<KeyValueModel, Object> endValues, Map<AnimatableField, KeyValueModel> keyValues, Rectangle2D position) {
        endValues.put(keyValues.get(LAYOUT_X), position.getMinX());
        endValues.put(keyValues.get(LAYOUT_Y), position.getMinY());
        endValues.put(keyValues.get(WIDTH), position.getWidth());
        endValues.put(keyValues.get(HEIGHT), position.getHeight());
    }

    private boolean anyNodeMoved(Map<KeyValueModel, Object> endValues) {
        return startValues.entrySet().stream().anyMatch(e -> !Objects.equals(e.getValue(), endValues.get(e.getKey())));
    }

    private void subscribeToEvents(EventBus eventBus) {
        eventBus.register(this);
    }
}
