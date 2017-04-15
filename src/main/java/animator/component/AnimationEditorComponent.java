package animator.component;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import animator.component.dialog.SaveDialogComponent;
import animator.component.menu.MenuBarComponent;
import animator.component.player.PlayerComponent;
import animator.component.property.PropertyEditorComponent;
import animator.component.scene.SceneComponent;
import animator.component.timeline.TimelineEditorComponent;
import animator.event.LifecycleEvent;
import animator.persist.PropertyKey;
import animator.persist.PropertyStore;
import animator.util.MathFunctions;

import javax.inject.Inject;

public class AnimationEditorComponent {

    private static final double DEFAULT_DIVIDER_POSITION = 0.75;

    private final StackPane root = new StackPane();
    private final VBox rootBox = new VBox();
    private final SplitPane outerPane = new SplitPane();
    private final MenuBarComponent menuBar;
    private final TimelineEditorComponent timelineEditor;
    private final PropertyEditorComponent propertyEditor;
    private final SceneComponent scene;
    private final PlayerComponent player;
    private final PropertyStore propertyStore;
    private final SaveDialogComponent saveDialogComponent;

    private double initialDividerPosition;

    @Inject
    public AnimationEditorComponent(MenuBarComponent menuBar, AnimationEditorPresenter presenter, TimelineEditorComponent timelineEditor,
                                    PropertyEditorComponent propertyEditor, SceneComponent scene, PlayerComponent player, PropertyStore propertyStore,
                                    SaveDialogComponent saveDialogComponent, EventBus eventBus) {

        this.menuBar = menuBar;
        this.timelineEditor = timelineEditor;
        this.propertyEditor = propertyEditor;
        this.scene = scene;
        this.player = player;
        this.propertyStore = propertyStore;
        this.saveDialogComponent = saveDialogComponent;
        initUi();
        initPresenter(presenter);
        configureDividerPosition();
        subscribeToEvents(eventBus);
    }

    public Parent getRoot() {
        return root;
    }

    @Subscribe
    public void onLifecycleEvent(LifecycleEvent event) {
        if (event == LifecycleEvent.STAGE_ABOUT_TO_SHOW) {
            outerPane.getDividers().get(0).setPosition(initialDividerPosition);
            root.requestFocus();
        }
    }

    private void initUi() {
        VBox innerPane = new VBox();
        innerPane.setMinWidth(0);
        innerPane.setMaxWidth(Double.MAX_VALUE);

        VBox timelineBox = new VBox(player.getRoot(), timelineEditor.getRoot());
        VBox.setVgrow(outerPane, Priority.ALWAYS);
        VBox.setVgrow(scene.getRoot(), Priority.ALWAYS);
        VBox.setVgrow(timelineEditor.getRoot(), Priority.ALWAYS);

        rootBox.disableProperty().bind(saveDialogComponent.showingProperty());

        outerPane.getItems().addAll(innerPane, propertyEditor.getRoot());
        innerPane.getChildren().addAll(scene.getRoot(), timelineBox);
        rootBox.getChildren().addAll(menuBar.getRoot(), outerPane);
        root.getChildren().addAll(rootBox, saveDialogComponent.getOverlay(), saveDialogComponent.getRoot());
    }

    private void configureDividerPosition() {
        String stored = propertyStore.getProperty(PropertyKey.DIVIDER_POSITION);
        DoubleProperty dividerPosition = outerPane.getDividers().get(0).positionProperty();
        if (stored != null && MathFunctions.canParseDouble(stored)) {
            initialDividerPosition = Double.parseDouble(stored);
        } else {
            initialDividerPosition = DEFAULT_DIVIDER_POSITION;
        }
        dividerPosition.set(initialDividerPosition);
        dividerPosition.addListener((v, o, n) -> propertyStore.setProperty(PropertyKey.DIVIDER_POSITION, "" + n.doubleValue()));
    }

    private void initPresenter(AnimationEditorPresenter presenter) {
        presenter.setView(this);
    }

    private void subscribeToEvents(EventBus eventBus) {
        eventBus.register(this);
    }
}