package animator.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import animator.command.*;
import animator.component.dialog.SaveDialogComponent;
import animator.component.scene.panning.PanningHelper;
import animator.model.*;
import animator.persist.FilePersistence;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static animator.component.dialog.SaveDialogComponent.Mode.*;

public class MenuActionHandler {

    private static final String LOGO_FILE = "logo.fxa";
    private static final String SLACK_FILE = "slack.fxa";
    private static final String WOBBLES_FILE = "wobbles.fxa";

    private final SceneModel sceneModel;
    private final TimelineModel timelineModel;
    private final PanningHelper panningHelper;
    private final CommandStack commandStack;
    private final FilePersistence filePersistence;
    private final Clipboard clipboard;
    private final SaveDialogComponent saveDialogComponent;
    private final TimelineSceneSynchronizer timelineSceneSynchronizer;
    private final PlayerModel playerModel;

    @Inject
    public MenuActionHandler(SceneModel sceneModel, TimelineModel timelineModel, PanningHelper panningHelper, CommandStack commandStack, EventBus eventBus,
                             FilePersistence filePersistence, Clipboard clipboard, SaveDialogComponent saveDialogComponent,
                             TimelineSceneSynchronizer timelineSceneSynchronizer, PlayerModel playerModel) {

        this.sceneModel = sceneModel;
        this.timelineModel = timelineModel;
        this.panningHelper = panningHelper;
        this.commandStack = commandStack;
        this.filePersistence = filePersistence;
        this.clipboard = clipboard;
        this.saveDialogComponent = saveDialogComponent;
        this.timelineSceneSynchronizer = timelineSceneSynchronizer;
        this.playerModel = playerModel;
        subscribeToEvents(eventBus);
    }

    @Subscribe
    public void onMenuAction(MenuActionEvent menuAction) {
        playerModel.setPlayMode(false);
        if (timelineModel.getSelectedKeyFrame() == null || saveDialogComponent.isShowing()) {
            return;
        }
        switch (menuAction) {
            case NEW:
                doWithSaveCheck(filePersistence::close, false);
                break;
            case OPEN:
                doWithSaveCheck(() -> {
                    if (filePersistence.load()) {
                        panningHelper.panToContent();
                    }
                }, false);
                break;
            case SAVE:
                filePersistence.save();
                break;
            case SAVE_AS:
                filePersistence.saveAs();
                break;
            case OPEN_LOGO:
                doWithSaveCheck(() -> {
                    if (filePersistence.loadInternal(LOGO_FILE)) {
                        panningHelper.panToContent();
                    }
                }, false);
                break;
            case OPEN_SLACK:
                doWithSaveCheck(() -> {
                    if (filePersistence.loadInternal(SLACK_FILE)) {
                        panningHelper.panToContent();
                    }
                }, false);
                break;
            case EXIT:
                doWithSaveCheck(Platform::exit, true);
                break;
            case UNDO:
                commandStack.undo();
                break;
            case REDO:
                commandStack.redo();
                break;
            case CUT:
                clipboard.cut();
                break;
            case COPY:
                clipboard.copy();
                break;
            case PASTE:
                clipboard.paste();
                break;
            case SELECT_ALL:
                sceneModel.getNodes().forEach(n -> n.setSelected(true));
                break;
            case DELETE_SELECTION:
                if (sceneModel.getSelectedNodes().count() > 0) {
                    commandStack.appendAndExecute(new DeleteNodeCommand(sceneModel, timelineModel, timelineSceneSynchronizer));
                }
                break;
            case CLEAR_SELECTION:
                sceneModel.getSelectedNodes().forEach(n -> n.setSelected(false));
                break;
            case TO_FRONT:
                if (!ReorderCommand.isSelectionAtFront(sceneModel)) {
                    commandStack.appendAndExecute(new ReorderCommand(sceneModel, ReorderCommand.Type.FRONT));
                }
                break;
            case TO_BACK:
                if (!ReorderCommand.isSelectionAtBack(sceneModel)) {
                    commandStack.appendAndExecute(new ReorderCommand(sceneModel, ReorderCommand.Type.BACK));
                }
                break;
            case GROUP:
                if (sceneModel.getSelectedNodes().count() > 1) {
                    List<NodeModel> selection = sceneModel.getSelectedNodes().collect(Collectors.toList());
                    commandStack.appendAndExecute(new GroupCommand(sceneModel, timelineModel, timelineSceneSynchronizer, new GroupModel(), selection));
                }
                break;
            case UNGROUP:
                List<NodeModel> selection = sceneModel.getSelectedNodes().collect(Collectors.toList());
                if (selection.size() == 1 && selection.get(0) instanceof GroupModel) {
                    commandStack.appendAndExecute(new UngroupCommand((GroupModel) selection.get(0), sceneModel, timelineModel, timelineSceneSynchronizer));
                }
                break;
            case ADD_ELEMENT:
                timelineModel.setSelectedKeyFrame(timelineModel.getKeyFrames().get(0));
                RectangleModel rectangle = new RectangleModel();
                AddRectangleCommand command = new AddRectangleCommand(rectangle, sceneModel, timelineModel, timelineSceneSynchronizer);
                panningHelper.setInitialPosition(command.getKeyValues().get(timelineModel.getKeyFrames().get(0)));
                commandStack.appendAndExecute(command);
                break;
        }
    }

    private void doWithSaveCheck(Runnable runnable, boolean exit) {
        if (filePersistence.isDirty()) {
            SaveDialogComponent.Mode mode;
            if (exit) {
                mode = filePersistence.isFileOpen() ? EXIT_FILE_OPEN : EXIT_NO_FILE_OPEN;
            } else {
                mode = filePersistence.isFileOpen() ? CLOSE_FILE_OPEN : CLOSE_NO_FILE_OPEN;
            }
            saveDialogComponent.show(mode).whenComplete((selection, exception) -> {
                if (selection == SaveDialogComponent.Selection.SAVE) {
                    filePersistence.save();
                    runnable.run();
                } else if (selection == SaveDialogComponent.Selection.CLOSE) {
                    runnable.run();
                }
            });
        } else {
            runnable.run();
        }
    }

    private void subscribeToEvents(EventBus eventBus) {
        eventBus.register(this);
    }
}
