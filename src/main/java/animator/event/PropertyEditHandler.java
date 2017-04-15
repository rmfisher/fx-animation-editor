package animator.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import animator.command.*;
import animator.model.AnimatableField;
import animator.model.KeyValueModel;
import animator.model.NodeModel;
import animator.model.TimelineModel;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static animator.event.PropertyEditEvent.Type.*;
import static animator.util.MapFunctions.filterByKeyAndForEach;
import static animator.util.MapFunctions.filterByKeyCollectAndMap;

public class PropertyEditHandler {

    private final Map<KeyValueModel, Object> startValues = new HashMap<>();
    private final CommandStack commandStack;
    private final TimelineModel timelineModel;

    @Inject
    public PropertyEditHandler(CommandStack commandStack, TimelineModel timelineModel, EventBus eventBus) {
        this.commandStack = commandStack;
        this.timelineModel = timelineModel;
        subscribeToEvents(eventBus);
    }

    @Subscribe
    public void onPropertyChange(PropertyEditEvent event) {
        if (timelineModel.getSelectedKeyFrame() == null || !anySelectedNodeContainsField(event.getField())) {
            return;
        }
        if (event.getType() == START) {
            filterByKeyAndForEach(getKeyValues(), NodeModel::isSelected, v -> storeStartKeyValues(v, event));
        } else if (event.getType() == UPDATE) {
            filterByKeyAndForEach(getKeyValues(), NodeModel::isSelected, v -> setNewValues(v, event));
        } else if (event.getType() == FINISH || event.getType() == SINGLE) {
            KeyValueChangeCommand command = createKeyValueChangeCommand(event);
            if (changeOccurred(command.getKeyValueChanges())) {
                commandStack.appendAndExecute(command);
            }
            startValues.clear();
        }
    }

    @Subscribe
    public void onInterpolatorChange(InterpolatorEditEvent change) {
        if (timelineModel.getSelectedKeyFrame() == null || !anySelectedNodeContainsField(change.getField())) {
            return;
        }
        InterpolatorChangeCommand command = createInterpolatorChangeCommand(change);
        if (interpolatorChangeOccurred(command.getInterpolatorChanges())) {
            commandStack.appendAndExecute(command);
        }
    }

    private void storeStartKeyValues(Map<AnimatableField, KeyValueModel> keyValues, PropertyEditEvent change) {
        filterByKeyAndForEach(keyValues, k -> k == change.getField(), v -> startValues.put(v, v.getValue()));
    }

    private Map<NodeModel, Map<AnimatableField, KeyValueModel>> getKeyValues() {
        return timelineModel.getSelectedKeyFrame().getKeyValues();
    }

    private void setNewValues(Map<AnimatableField, KeyValueModel> input, PropertyEditEvent event) {
        filterByKeyAndForEach(input, k -> k == event.getField(), v -> v.setValue(event.getNewValue()));
    }

    private KeyValueChangeCommand createKeyValueChangeCommand(PropertyEditEvent event) {
        Map<NodeModel, KeyValueChange> changes = filterByKeyCollectAndMap(getKeyValues(), n -> selectedAndHasField(n, event), v -> createChange(v, event));
        return new KeyValueChangeCommand(changes, timelineModel);
    }

    private KeyValueChange createChange(Map<AnimatableField, KeyValueModel> input, PropertyEditEvent event) {
        KeyValueModel keyValue = input.get(event.getField());
        Object oldValue = event.getType() == FINISH ? startValues.get(keyValue) : keyValue.getValue();
        return new KeyValueChange(keyValue, oldValue, event.getNewValue());
    }

    private InterpolatorChangeCommand createInterpolatorChangeCommand(InterpolatorEditEvent event) {
        Map<NodeModel, InterpolatorChange> changes = filterByKeyCollectAndMap(getKeyValues(), n -> selectedAndHasField(n, event), v -> createChange(v, event));
        return new InterpolatorChangeCommand(changes, timelineModel);
    }

    private InterpolatorChange createChange(Map<AnimatableField, KeyValueModel> input, InterpolatorEditEvent event) {
        KeyValueModel keyValue = input.get(event.getField());
        return new InterpolatorChange(keyValue, keyValue.getInterpolator(), event.getInterpolator());
    }
    private boolean changeOccurred(Map<NodeModel, KeyValueChange> keyValueChanges) {
        return keyValueChanges.values().stream().anyMatch(c -> !Objects.equals(c.getOldValue(), c.getNewValue()));
    }

    private boolean interpolatorChangeOccurred(Map<NodeModel, InterpolatorChange> interpolatorChanges) {
        return interpolatorChanges.values().stream().anyMatch(c -> !Objects.equals(c.getOldValue(), c.getNewValue()));
    }

    private boolean selectedAndHasField(NodeModel node, PropertyEditEvent event) {
        return node.isSelected() && getKeyValues().get(node).containsKey(event.getField());
    }

    private boolean selectedAndHasField(NodeModel node, InterpolatorEditEvent event) {
        return node.isSelected() && getKeyValues().get(node).containsKey(event.getField());
    }

    private boolean anySelectedNodeContainsField(AnimatableField field) {
        return getKeyValues().keySet().stream().anyMatch(n -> n.isSelected() && getKeyValues().get(n).containsKey(field));
    }

    private void subscribeToEvents(EventBus eventBus) {
        eventBus.register(this);
    }
}
