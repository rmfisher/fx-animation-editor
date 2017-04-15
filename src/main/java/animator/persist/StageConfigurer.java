package animator.persist;

import javafx.stage.Stage;

import javax.inject.Inject;

public class StageConfigurer {

    private PropertyStore propertyStore;

    @Inject
    public StageConfigurer(PropertyStore propertyStore) {
        this.propertyStore = propertyStore;
    }

    public void initialize(Stage stage) {
        String maximized = propertyStore.getProperty(PropertyKey.STAGE_MAXIMIZED);
        if (maximized != null && Boolean.parseBoolean(maximized)) {
            stage.setMaximized(true);
        }
        stage.maximizedProperty().addListener((v, o, n) -> propertyStore.setProperty(PropertyKey.STAGE_MAXIMIZED, "" + n));
    }
}
