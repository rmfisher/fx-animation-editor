package animator.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import animator.command.AddKeyFrameCommand;
import animator.command.CommandStack;
import animator.command.DeleteKeyFrameCommand;
import animator.model.*;
import animator.util.ModelFunctions;

import javax.inject.Inject;
import java.util.Map;

public class TimelineHandler {

    private static final double INITIAL_KEY_FRAME_TIME = 1;

    private final TimelineModel timelineModel;
    private final CommandStack commandStack;

    @Inject
    public TimelineHandler(TimelineModel timelineModel, CommandStack commandStack, EventBus eventBus) {
        this.timelineModel = timelineModel;
        this.commandStack = commandStack;
        subscribeToEvents(eventBus);
    }

    @Subscribe
    public void handleTimelineEvent(TimelineEvent event) {
        switch (event.getType()) {
            case ADD:
                commandStack.appendAndExecute(new AddKeyFrameCommand(createNewKeyFrame(), timelineModel));
                break;
            case DELETE:
                commandStack.appendAndExecute(new DeleteKeyFrameCommand(event.getKeyFrame(), timelineModel));
                break;
        }
    }

    private KeyFrameModel createNewKeyFrame() {
        KeyFrameModel keyFrame = new KeyFrameModel();
        keyFrame.setTime(INITIAL_KEY_FRAME_TIME);
        KeyFrameModel firstKeyFrame = timelineModel.getKeyFrames().get(0);
        firstKeyFrame.getKeyValues().keySet().forEach(node -> keyFrame.getKeyValues().put(node, createNullKeyValues(node)));
        return keyFrame;
    }

    private Map<AnimatableField, KeyValueModel> createNullKeyValues(NodeModel node) {
        Map<AnimatableField, KeyValueModel> keyValues = ModelFunctions.createKeyValues(node);
        keyValues.values().forEach(v -> v.setValue(null));
        return keyValues;
    }

    private void subscribeToEvents(EventBus eventBus) {
        eventBus.register(this);
    }
}
