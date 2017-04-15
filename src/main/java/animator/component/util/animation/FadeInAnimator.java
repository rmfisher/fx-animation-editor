package animator.component.util.animation;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeInAnimator {

    private static final String ANIMATION_TRIGGER_KEY = "opacity-animation-trigger";

    private final ChangeListener<Boolean> triggerChangeListener = (v, o, n) -> onTriggerChanged(n);
    private final FadeTransition fadeTransition = new FadeTransition();
    private final double from;
    private final double to;
    private final double durationInMillis;

    private ObservableBooleanValue trigger;
    private Node node;

    public FadeInAnimator() {
        this(0, 1, 200);
    }

    public FadeInAnimator(double from, double to, double durationInMillis) {
        this.from = from;
        this.to = to;
        this.durationInMillis = durationInMillis;
        fadeTransition.setOnFinished(event -> onFinished());
    }

    public void apply(ObservableBooleanValue trigger, Node node) {
        this.trigger = trigger;
        this.node = node;
        configureTransition(node);
        node.getProperties().put(ANIMATION_TRIGGER_KEY, trigger); // Prevents trigger from being garbage collected.
        trigger.addListener(triggerChangeListener);
        node.setOpacity(trigger.get() ? to : from);
    }

    public void dispose() {
        fadeTransition.stop();
        if (node != null) {
            node.getProperties().remove(ANIMATION_TRIGGER_KEY);
            node = null;
        }
        if (trigger != null) {
            trigger.removeListener(triggerChangeListener);
            trigger = null;
        }
    }

    private void configureTransition(Node node) {
        fadeTransition.setDuration(Duration.millis(durationInMillis));
        fadeTransition.setNode(node);
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
    }

    private void onTriggerChanged(boolean newValue) {
        fadeTransition.setRate(newValue ? 1 : -1);
        fadeTransition.play();
        node.setVisible(true);
    }

    private void onFinished() {
        if (node.getOpacity() == 0) {
            node.setVisible(false);
        }
    }
}
