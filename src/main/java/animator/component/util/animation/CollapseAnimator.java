package animator.component.util.animation;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CollapseAnimator {

    // Animation should play more slowly for longer distance up to a limit.
    private final double minDuration;
    private final double addedDurationPerPixelToTravel;
    private final double maxDuration;

    private Timeline timeline;
    private Runnable onFinished;

    public CollapseAnimator() {
        this(60, 0.4, 220);
    }

    public CollapseAnimator(double minDuration, double addedDurationPerPixelToTravel, double maxDuration) {
        this.minDuration = minDuration;
        this.addedDurationPerPixelToTravel = addedDurationPerPixelToTravel;
        this.maxDuration = maxDuration;
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    public void apply(ObservableBooleanValue expanded, ObservableBooleanValue animate, Region region) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(region.widthProperty());
        clip.heightProperty().bind(region.heightProperty());
        region.setClip(clip);
        region.setMinHeight(0);
        region.setMaxHeight(expanded.get() ? Region.USE_PREF_SIZE : 0);
        region.visibleProperty().bind(region.heightProperty().greaterThan(0));

        expanded.addListener((v, o, n) -> onExpandedChanged(n, animate.get(), region));
    }

    private void onExpandedChanged(boolean expanded, boolean animate, Region region) {
        if (animate && region.getScene() != null) {
            animateToTarget(expanded, region);
        } else {
            region.setMaxHeight(expanded ? Region.USE_PREF_SIZE : 0);
            notifyOnFinished();
        }
    }

    private void animateToTarget(boolean expanded, Region region) {
        if (timeline != null) {
            timeline.stop();
        }
        double prefHeight = region.prefHeight(-1);
        if (region.getMaxHeight() < 0) {
            region.setMaxHeight(prefHeight);
        }
        double targetHeight = expanded ? prefHeight : 0;
        KeyValue keyValue = new KeyValue(region.maxHeightProperty(), targetHeight, Interpolator.EASE_BOTH);
        KeyFrame keyFrame = new KeyFrame(getAnimationDuration(targetHeight, region), keyValue);
        timeline = new Timeline(keyFrame);
        timeline.setOnFinished(event -> onTimelineFinished(region, expanded));
        timeline.play();
    }

    private Duration getAnimationDuration(double targetHeight, Region region) {
        double distanceToTravel = Math.abs(targetHeight - region.getHeight());
        double time = Math.min(minDuration + distanceToTravel * addedDurationPerPixelToTravel, maxDuration);
        return Duration.millis(time);
    }

    private void onTimelineFinished(Region region, boolean expanded) {
        if (expanded) {
            region.setMaxHeight(Region.USE_PREF_SIZE);
        }
        notifyOnFinished();
    }

    private void notifyOnFinished() {
        if (onFinished != null) {
            onFinished.run();
        }
    }
}
