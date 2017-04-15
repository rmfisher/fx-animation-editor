package animator.component.timeline;

import com.google.common.eventbus.EventBus;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import animator.command.CommandStack;
import animator.command.MoveKeyFrameCommand;
import animator.event.TimelineEvent;
import animator.model.KeyFrameModel;
import animator.model.PlayerModel;
import animator.model.TimelineModel;

import javax.inject.Inject;

import static animator.component.util.binding.BindingFunctions.bind;


public class TimelineEditorPresenter {

    private final ChangeListener<Number> absoluteTimeUpdater = (v, o, n) -> updateAbsoluteTimes();
    private final TimelineModel model;
    private final PlayerModel playerModel;
    private final EventBus eventBus;
    private final CommandStack commandStack;
    private final KeyFrameDragAnimator keyFrameDragAnimator;
    private TimelineEditorComponent view;

    @Inject
    public TimelineEditorPresenter(TimelineModel model, PlayerModel playerModel, EventBus eventBus, CommandStack commandStack,
                                   KeyFrameDragAnimator keyFrameDragAnimator) {

        this.model = model;
        this.playerModel = playerModel;
        this.eventBus = eventBus;
        this.commandStack = commandStack;
        this.keyFrameDragAnimator = keyFrameDragAnimator;
    }

    void setView(TimelineEditorComponent view) {
        this.view = view;
        bindModelAndView();
        bindDisabledState();
        addActionHandlers();
        model.selectedKeyFrameProperty().addListener((v, o, n) -> onSelectionChanged(o, n));
        model.getKeyFrames().addListener(this::addListenersToTrackAbsoluteTimes);
        addZeroKeyFrame();
    }

    private void bindModelAndView() {
        bind(view.getKeyFramesBox().getChildren(), model.getKeyFrames(), this::createKeyFrameComponent);
        keyFrameDragAnimator.initialize(view.getKeyFramesBox(), this::onKeyFrameRepositioned);
    }

    private void bindDisabledState() {
        view.getScrollPaneContent().mouseTransparentProperty().bind(playerModel.playModeProperty());
    }

    private Node createKeyFrameComponent(KeyFrameModel keyFrame) {
        KeyFrameComponent keyFrameComponent = new KeyFrameComponent(keyFrame, commandStack);
        keyFrameComponent.getDeleteButton().setOnAction(event -> deleteKeyFrame(keyFrame));
        keyFrameComponent.getRoot().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onKeyFrameComponentPressed(event, keyFrame));
        if (keyFrame.getTime() > 0) {
            keyFrameDragAnimator.addHandlers(keyFrameComponent.getRoot());
        }
        return keyFrameComponent.getRoot();
    }

    private void onKeyFrameComponentPressed(MouseEvent event, KeyFrameModel keyFrame) {
        if (event.getButton() == MouseButton.PRIMARY) {
            model.setSelectedKeyFrame(keyFrame);
        }
    }

    private void addActionHandlers() {
        view.getAddButton().setOnAction(event -> addKeyFrame());
    }

    private void onSelectionChanged(KeyFrameModel oldValue, KeyFrameModel newValue) {
        if (oldValue != null) {
            oldValue.setSelected(false);
        }
        if (newValue != null) {
            newValue.setSelected(true);
        }
        playerModel.setCurrentTime(newValue != null ? newValue.getAbsoluteTime() : 0);
    }

    private void addZeroKeyFrame() {
        KeyFrameModel keyFrame = new KeyFrameModel();
        model.getKeyFrames().add(keyFrame);
        model.setSelectedKeyFrame(keyFrame);
    }

    private void addKeyFrame() {
        eventBus.post(new TimelineEvent(TimelineEvent.Type.ADD, null));
        view.scrollToEnd();
    }

    private void deleteKeyFrame(KeyFrameModel keyFrame) {
        int index = model.getKeyFrames().indexOf(keyFrame);
        if (index > 0) {
            eventBus.post(new TimelineEvent(TimelineEvent.Type.DELETE, keyFrame));
            view.getRoot().requestFocus();
        }
    }

    private void addListenersToTrackAbsoluteTimes(ListChangeListener.Change<? extends KeyFrameModel> change) {
        while (change.next()) {
            if (change.wasAdded()) {
                change.getAddedSubList().forEach(f -> f.timeProperty().addListener(absoluteTimeUpdater));
                updateAbsoluteTimes();
            } else if (change.wasRemoved()) {
                change.getRemoved().forEach(f -> f.timeProperty().removeListener(absoluteTimeUpdater));
            }
        }
    }

    private void updateAbsoluteTimes() {
        double absoluteTime = 0;
        for (KeyFrameModel keyFrame : model.getKeyFrames()) {
            absoluteTime += keyFrame.getTime();
            keyFrame.setAbsoluteTime(absoluteTime);
        }
    }

    private void onKeyFrameRepositioned(Integer oldIndex, Integer newIndex) {
        commandStack.appendAndExecute(new MoveKeyFrameCommand(model, oldIndex, newIndex));
    }
}
