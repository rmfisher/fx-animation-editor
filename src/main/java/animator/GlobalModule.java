package animator;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import animator.command.CommandStack;
import animator.component.dialog.FileChooserComponent;
import animator.component.dialog.SaveDialogComponent;
import animator.component.scene.panning.PanningComponent;
import animator.component.scene.selection.ResizerComponent;
import animator.component.scene.selection.SelectionDragBehavior;
import animator.component.scene.selection.SelectionResizeBehavior;
import animator.event.*;
import animator.model.PlayerModel;
import animator.model.PropertyModel;
import animator.model.SceneModel;
import animator.model.TimelineModel;
import animator.model.interpolator.InterpolatorListModel;
import animator.persist.FilePersistence;
import animator.persist.PropertyStore;

import javax.inject.Singleton;

public class GlobalModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EventBus.class).in(Singleton.class);
        bind(CommandStack.class).in(Singleton.class);
        bind(PropertyStore.class).in(Singleton.class);
        bind(TimelineModel.class).in(Singleton.class);
        bind(SceneModel.class).in(Singleton.class);
        bind(InterpolatorListModel.class).in(Singleton.class);
        bind(PropertyModel.class).in(Singleton.class);
        bind(PanningComponent.class).in(Singleton.class);
        bind(ResizerComponent.class).in(Singleton.class);
        bind(PlayerModel.class).in(Singleton.class);
        bind(FileChooserComponent.class).in(Singleton.class);
        bind(FilePersistence.class).in(Singleton.class);
        bind(SaveDialogComponent.class).in(Singleton.class);

        bind(MenuActionHandler.class).asEagerSingleton();
        bind(PropertyEditHandler.class).asEagerSingleton();
        bind(TimelineHandler.class).asEagerSingleton();
        bind(SelectionDragBehavior.class).asEagerSingleton();
        bind(SelectionDragHandler.class).asEagerSingleton();
        bind(SelectionResizeBehavior.class).asEagerSingleton();
        bind(SelectionResizeHandler.class).asEagerSingleton();
        bind(TimelineSceneSynchronizer.class).asEagerSingleton();
        bind(ScenePropertySynchronizer.class).asEagerSingleton();
        bind(PlayerHandler.class).asEagerSingleton();
        bind(TimelineDurationTracker.class).asEagerSingleton();
    }
}