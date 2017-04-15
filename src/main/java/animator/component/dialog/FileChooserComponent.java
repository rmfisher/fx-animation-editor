package animator.component.dialog;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import animator.persist.PropertyKey;
import animator.persist.PropertyStore;

import javax.inject.Inject;
import java.io.File;

public class FileChooserComponent {

    private static final String USER_HOME = "user.home";
    private static final String SAVE_TITLE = "Save";
    private static final String OPEN_TITLE = "Open";
    private static final String FILE_DESCRIPTION = "FX Animation";
    private static final String EXTENSION = "*.fxa";

    private final FileChooser fileChooser = new FileChooser();
    private final PropertyStore propertyStore;

    private File lastDirectory;
    private Window owner;

    @Inject
    public FileChooserComponent(PropertyStore propertyStore) {
        this.propertyStore = propertyStore;
        init();
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    public File chooseFile(Type chooserType) {
        if (lastDirectory != null && lastDirectory.exists()) {
            fileChooser.setInitialDirectory(lastDirectory);
        }
        fileChooser.setTitle(chooserType == Type.SAVE ? SAVE_TITLE : OPEN_TITLE);
        File file = chooserType == Type.SAVE ? fileChooser.showSaveDialog(owner) : chooserType == Type.OPEN ? fileChooser.showOpenDialog(owner) : null;
        updateLastDirectory(file);
        return file;
    }

    private void init() {
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(FILE_DESCRIPTION, EXTENSION);
        fileChooser.getExtensionFilters().addAll(extensionFilter);
        fileChooser.setSelectedExtensionFilter(extensionFilter);

        String savedLastDirectory = propertyStore.getProperty(PropertyKey.LAST_DIRECTORY);
        if (savedLastDirectory != null && new File(savedLastDirectory).isDirectory()) {
            lastDirectory = new File(savedLastDirectory);
        } else if (System.getProperty(USER_HOME) != null) {
            lastDirectory = new File(System.getProperty(USER_HOME));
        }
    }

    private void updateLastDirectory(File file) {
        if (file != null) {
            lastDirectory = file.getParentFile();
            propertyStore.setProperty(PropertyKey.LAST_DIRECTORY, lastDirectory.getPath());
        }
    }

    public enum Type {
        SAVE, OPEN
    }
}
