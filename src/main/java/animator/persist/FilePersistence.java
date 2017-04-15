package animator.persist;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import animator.command.CommandStack;
import animator.component.dialog.FileChooserComponent;
import animator.component.util.binding.ChainedBindingFunctions;
import animator.model.SceneModel;
import animator.model.TimelineModel;
import animator.model.interpolator.InterpolatorListModel;
import animator.persist.model.PAnimationModel;

import javax.inject.Inject;
import java.io.*;

public class FilePersistence {

    private static final String TITLE_BASE = "JavaFX Animation Editor - ";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final TimelineModel timelineModel;
    private final SceneModel sceneModel;
    private final InterpolatorListModel interpolatorListModel;
    private final CommandStack commandStack;
    private final FileChooserComponent fileChooserComponent;
    private final PropertyStore propertyStore;
    private final ObjectProperty<File> editedFile = new SimpleObjectProperty<>();
    private final BooleanProperty dirty = new SimpleBooleanProperty();

    @Inject
    public FilePersistence(TimelineModel timelineModel, SceneModel sceneModel, InterpolatorListModel interpolatorListModel, CommandStack commandStack,
                           FileChooserComponent fileChooserComponent, PropertyStore propertyStore) {

        this.timelineModel = timelineModel;
        this.sceneModel = sceneModel;
        this.interpolatorListModel = interpolatorListModel;
        this.commandStack = commandStack;
        this.fileChooserComponent = fileChooserComponent;
        this.propertyStore = propertyStore;
        commandStack.addListener(command -> dirty.set(!commandStack.isSavePoint()));
        editedFile.addListener((v, o, n) -> storeEditedFile(n));
    }

    public void close() {
        editedFile.set(null);
        sceneModel.getNodes().forEach(n -> n.setSelected(false));
        timelineModel.setSelectedKeyFrame(timelineModel.getKeyFrames().get(0));
        timelineModel.getKeyFrames().remove(1, timelineModel.getKeyFrames().size());
        timelineModel.getKeyFrames().get(0).getKeyValues().clear();
        sceneModel.getNodes().clear();
        commandStack.clear();
        dirty.set(false);
    }

    public boolean save() {
        if (editedFile.get() != null && editedFile.get().exists() && editedFile.get().canWrite()) {
            return save(editedFile.get());
        } else {
            return saveAs();
        }
    }

    public boolean saveAs() {
        File file = fileChooserComponent.chooseFile(FileChooserComponent.Type.SAVE);
        if (file != null) {
            boolean didSave = save(file);
            if (didSave) {
                editedFile.set(file);
            }
            return didSave;
        }
        return false;
    }

    public boolean load() {
        return load(fileChooserComponent.chooseFile(FileChooserComponent.Type.OPEN));
    }

    public boolean loadInternal(String file) {
        close();
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(file), Charsets.UTF_8)) {
            load(reader);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean loadLastEditedFileIfExists() {
        String lastEditedFilePath = propertyStore.getProperty(PropertyKey.LAST_EDITED_FILE);
        if (lastEditedFilePath != null) {
            File lastEditedFile = new File(lastEditedFilePath);
            if (lastEditedFile.exists()) {
                return load(lastEditedFile);
            }
        }
        return false;
    }

    public StringExpression getTitle() {
        StringBinding fileName = ChainedBindingFunctions.from(editedFile).mapToString(File::getName);
        StringBinding titleText = Bindings.when(editedFile.isNotNull()).then(fileName).otherwise("Untitled");
        StringBinding dirtyFlag = Bindings.when(dirty).then("*").otherwise("");
        return Bindings.concat(TITLE_BASE, titleText, dirtyFlag);
    }

    public boolean isFileOpen() {
        return editedFile.get() != null;
    }

    public boolean isDirty() {
        return dirty.get();
    }

    public ReadOnlyBooleanProperty dirtyProperty() {
        return dirty;
    }

    private boolean save(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            PAnimationModel toSave = ModelConverter.fromFx(timelineModel, sceneModel, interpolatorListModel);
            gson.toJson(toSave, fileWriter);
            commandStack.markSavePoint();
            dirty.set(false);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean load(File file) {
        if (file != null) {
            try (FileReader reader = new FileReader(file)) {
                load(reader);
                editedFile.set(file);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void load(Reader reader) {
        PAnimationModel loadedModel = gson.fromJson(reader, PAnimationModel.class);
        ModelConverter.toFx(timelineModel, sceneModel, interpolatorListModel, loadedModel);
        commandStack.clear();
        dirty.set(false);
    }

    private void storeEditedFile(File editedFile) {
        propertyStore.setProperty(PropertyKey.LAST_EDITED_FILE, editedFile != null ? editedFile.getAbsolutePath() : null);
    }
}
