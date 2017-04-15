package animator.util;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;

public class RateLimitedChangeListener<T> implements ChangeListener<T> {

    private final ChangeListener<T> delegate;
    private final Timeline timer;

    private T lastDelegated;
    private ObservableValue<? extends T> lastObservable;
    private T queued;
    private boolean queuePopulated;

    public RateLimitedChangeListener(ChangeListener<T> delegate) {
        this(delegate, Duration.millis(200));
    }

    public RateLimitedChangeListener(ChangeListener<T> delegate, Duration delay) {
        this.delegate = delegate;
        this.timer = new Timeline(new KeyFrame(delay));
        timer.setOnFinished(event -> onTimerFinished());
    }

    @Override
    public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
        if (timer.getStatus().equals(Animation.Status.STOPPED)) {
            delegate.changed(observable, oldValue, newValue);
            lastDelegated = newValue;
            lastObservable = observable;
            timer.playFromStart();
        } else {
            queued = newValue;
            queuePopulated = true;
        }
    }

    private void onTimerFinished() {
        if (queuePopulated) {
            delegate.changed(lastObservable, lastDelegated, queued);
            lastDelegated = queued;
            queued = null;
            queuePopulated = false;
            timer.playFromStart();
        }
    }
}
