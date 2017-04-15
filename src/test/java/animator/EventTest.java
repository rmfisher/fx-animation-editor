package animator;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import animator.command.CommandStack;
import animator.component.dialog.FileChooserComponent;
import animator.component.dialog.SaveDialogComponent;
import animator.component.scene.panning.PanningHelper;
import animator.event.*;
import animator.model.*;
import animator.model.interpolator.*;
import animator.util.ModelFunctions;

import javax.inject.Singleton;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static animator.event.PropertyEditEvent.Type.SINGLE;

/**
 * Posts a large number of random events and checks that no exception occurs.
 */
public class EventTest {

    private static final int NUMBER_OF_RANDOM_ACTIONS = 10000;
    private static final String SAVE_FILE_NAME = "testSaveFile.fxa";

    private final List<MenuActionEvent> actions = new ArrayList<>(Arrays.asList(MenuActionEvent.values()));
    private final List<AnimatableField> fields = Arrays.asList(AnimatableField.values());
    private final Random random = new Random();

    private EventBus eventBus;
    private SceneModel sceneModel;
    private TimelineModel timelineModel;
    private File saveFile;
    private boolean exceptionOccurred;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new EventTestModule());
        eventBus = injector.getInstance(EventBus.class);
        sceneModel = injector.getInstance(SceneModel.class);
        timelineModel = injector.getInstance(TimelineModel.class);
        addInterpolators(injector.getInstance(InterpolatorListModel.class));
        configureMockFileChooser(injector.getInstance(FileChooserComponent.class));
        configureMockSaveDialog(injector.getInstance(SaveDialogComponent.class));

        KeyFrameModel keyFrame = new KeyFrameModel();
        timelineModel.getKeyFrames().add(keyFrame);
        timelineModel.setSelectedKeyFrame(keyFrame);

        actions.remove(MenuActionEvent.EXIT);
    }

    @Test
    public void testRandomEvents() {
        System.out.println("Posting " + NUMBER_OF_RANDOM_ACTIONS + " random events...");
        for (int i = 0; i < NUMBER_OF_RANDOM_ACTIONS; i++) {
            doSomethingRandom();
            Assert.assertFalse(exceptionOccurred);

            long timelineNodeCount = timelineModel.getKeyFrames().get(0).getKeyValues().size();
            long sceneNodeCount = ModelFunctions.getAllSceneNodes(sceneModel).count();
            Assert.assertEquals(timelineNodeCount, sceneNodeCount);

            Assert.assertNotNull(timelineModel.getSelectedKeyFrame());
        }
        if (saveFile != null) {
            saveFile.delete();
        }
        System.out.println("Ok");
    }

    private void doSomethingRandom() {
        int r = random.nextInt(25);
        if (r < 15) {
            // Post a random menu action e.g. add rectangle, undo, etc.
            eventBus.post(actions.get(random.nextInt(actions.size())));
        } else if (r < 18) {
            // Randomly change what nodes are selected.
            sceneModel.getNodes().forEach(n -> n.setSelected(random.nextBoolean()));
        } else if (r < 19) {
            // Add a key frame.
            eventBus.post(new TimelineEvent(TimelineEvent.Type.ADD, null));
        } else if (r < 20) {
            // Delete a random key frame (except the first one at t=0).
            int keyFrameCount = timelineModel.getKeyFrames().size();
            if (keyFrameCount > 1) {
                eventBus.post(new TimelineEvent(TimelineEvent.Type.DELETE, timelineModel.getKeyFrames().get(random.nextInt(keyFrameCount - 1) + 1)));
            }
        } else if (r < 21) {
            // Select a random key frame.
            timelineModel.setSelectedKeyFrame(timelineModel.getKeyFrames().get(random.nextInt(timelineModel.getKeyFrames().size())));
        } else if (r < 23) {
            // Drag a random node if there are any nodes present.
            sceneModel.getNodes().forEach(n -> n.setSelected(false));
            if (!sceneModel.getNodes().isEmpty()) {
                eventBus.post(new SelectionDragEvent(SelectionDragEvent.Type.START, null));
                eventBus.post(createSelectionDragEvent());
            }
        } else {
            // Edit a random property.
            AnimatableField field = fields.get(random.nextInt(fields.size()));
            eventBus.post(new PropertyEditEvent(SINGLE, field, createRandomValue(field)));
        }
    }

    private Object createRandomValue(AnimatableField field) {
        if (random.nextInt(10) == 0) {
            return null;
        } else if (field == AnimatableField.STROKE || field == AnimatableField.FILL) {
            return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        } else {
            return random.nextDouble();
        }
    }

    private SelectionDragEvent createSelectionDragEvent() {
        NodeModel node = sceneModel.getNodes().get(random.nextInt(sceneModel.getNodes().size()));
        node.setSelected(true);
        Map<NodeModel, Point2D> end = new HashMap<>();
        end.put(node, new Point2D(100 * random.nextDouble(), 100 * random.nextDouble()));
        return new SelectionDragEvent(SelectionDragEvent.Type.FINISHED, end);
    }

    private void addInterpolators(InterpolatorListModel interpolatorListModel) {
        interpolatorListModel.getInterpolators().addAll(DefaultInterpolators.getAll());
        interpolatorListModel.getInterpolators().add(new SplineInterpolatorModel());
        interpolatorListModel.getInterpolators().add(new OvershootInterpolatorModel());
        interpolatorListModel.getInterpolators().add(new BounceInterpolatorModel());
    }

    private void configureMockFileChooser(FileChooserComponent fileChooser) {
        when(fileChooser.chooseFile(FileChooserComponent.Type.SAVE)).thenAnswer(invocation -> {
            if (saveFile == null) {
                saveFile = new File(SAVE_FILE_NAME);
                saveFile.createNewFile();
            }
            return saveFile;
        });
        when(fileChooser.chooseFile(FileChooserComponent.Type.OPEN)).thenAnswer(invocation -> saveFile);
    }

    private void configureMockSaveDialog(SaveDialogComponent saveDialog) {
        Mockito.when(saveDialog.show(any())).thenReturn(CompletableFuture.completedFuture(SaveDialogComponent.Selection.CLOSE));
    }

    private class EventTestModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(EventBus.class).toInstance(new EventBus((exception, context) -> {
                exceptionOccurred = true;
                exception.printStackTrace();
            }));

            bind(CommandStack.class).in(Singleton.class);
            bind(TimelineModel.class).in(Singleton.class);
            bind(SceneModel.class).in(Singleton.class);
            bind(PropertyModel.class).in(Singleton.class);
            bind(InterpolatorListModel.class).in(Singleton.class);

            bind(MenuActionHandler.class).asEagerSingleton();
            bind(PropertyEditHandler.class).asEagerSingleton();
            bind(TimelineHandler.class).asEagerSingleton();
            bind(TimelineSceneSynchronizer.class).asEagerSingleton();
            bind(ScenePropertySynchronizer.class).asEagerSingleton();
            bind(SelectionDragHandler.class).asEagerSingleton();

            bind(FileChooserComponent.class).toInstance(Mockito.mock(FileChooserComponent.class));
            bind(PanningHelper.class).toInstance(Mockito.mock(PanningHelper.class));
            bind(SaveDialogComponent.class).toInstance(Mockito.mock(SaveDialogComponent.class));
        }
    }
}
