package animator.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TimelineModel {

    private final ListProperty<KeyFrameModel> keyFrames = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<KeyFrameModel> selectedKeyFrame = new SimpleObjectProperty<>();
    private final DoubleProperty totalTime = new SimpleDoubleProperty();

    public ObservableList<KeyFrameModel> getKeyFrames() {
        return keyFrames.get();
    }

    public ReadOnlyListProperty<KeyFrameModel> keyFramesProperty() {
        return keyFrames;
    }

    public KeyFrameModel getSelectedKeyFrame() {
        return selectedKeyFrame.get();
    }

    public ObjectProperty<KeyFrameModel> selectedKeyFrameProperty() {
        return selectedKeyFrame;
    }

    public void setSelectedKeyFrame(KeyFrameModel selectedFrame) {
        this.selectedKeyFrame.set(selectedFrame);
    }

    public double getTotalTime() {
        return totalTime.get();
    }

    public DoubleProperty totalTimeProperty() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime.set(totalTime);
    }
}
