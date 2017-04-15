package animator.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class PlayerModel {

    private final BooleanProperty playMode = new SimpleBooleanProperty();
    private final BooleanProperty running = new SimpleBooleanProperty();
    private final DoubleProperty currentTime = new SimpleDoubleProperty();
    private final DoubleProperty finalTime = new SimpleDoubleProperty();
    private final BooleanProperty animationPossible = new SimpleBooleanProperty();

    public boolean isPlayMode() {
        return playMode.get();
    }

    public BooleanProperty playModeProperty() {
        return playMode;
    }

    public void setPlayMode(boolean playMode) {
        this.playMode.set(playMode);
    }

    public boolean isRunning() {
        return running.get();
    }

    public BooleanProperty runningProperty() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public double getCurrentTime() {
        return currentTime.get();
    }

    public DoubleProperty currentTimeProperty() {
        return currentTime;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime.set(currentTime);
    }

    public double getFinalTime() {
        return finalTime.get();
    }

    public DoubleProperty finalTimeProperty() {
        return finalTime;
    }

    public void setFinalTime(double finalTime) {
        this.finalTime.set(finalTime);
    }

    public boolean isAnimationPossible() {
        return animationPossible.get();
    }

    public BooleanProperty animationPossibleProperty() {
        return animationPossible;
    }

    public void setAnimationPossible(boolean animationPossible) {
        this.animationPossible.set(animationPossible);
    }

}
