package animator;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import animator.component.AnimationEditorComponent;
import animator.component.dialog.FileChooserComponent;
import animator.component.scene.panning.PanningHelper;
import animator.event.LifecycleEvent;
import animator.event.MenuActionEvent;
import animator.persist.FilePersistence;
import animator.persist.StageConfigurer;
import animator.util.OsHelper;

public class AnimationEditorApp extends Application {

    private static final String STYLESHEET = "style.css";
    private static final String ICON = "icon.png";
    private static final double INITIAL_WIDTH = 800;
    private static final double INITIAL_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Injector injector = Guice.createInjector(new GlobalModule());
        FilePersistence filePersistence = injector.getInstance(FilePersistence.class);
        EventBus eventBus = injector.getInstance(EventBus.class);
        injector.getInstance(FileChooserComponent.class).setOwner(stage);

        Scene scene = new Scene(injector.getInstance(AnimationEditorComponent.class).getRoot(), INITIAL_WIDTH, INITIAL_HEIGHT);
        scene.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());

        stage.setScene(scene);
        if (OsHelper.isWindows()) {
            stage.getIcons().add(new Image(getClass().getResource(ICON).toExternalForm()));
        }
        stage.titleProperty().bind(filePersistence.getTitle());
        stage.setOnCloseRequest(event -> {
            eventBus.post(MenuActionEvent.EXIT);
            event.consume();
        });
        injector.getInstance(StageConfigurer.class).initialize(stage);
        stage.setOpacity(0);
        stage.show();

        eventBus.post(LifecycleEvent.STAGE_ABOUT_TO_SHOW);

        Platform.runLater(() -> {
            stage.setOpacity(1);
            if (filePersistence.loadLastEditedFileIfExists()) {
                injector.getInstance(PanningHelper.class).panToContent();
            }
        });
    }
}
