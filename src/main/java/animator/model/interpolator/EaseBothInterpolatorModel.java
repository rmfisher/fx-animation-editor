package animator.model.interpolator;

import javafx.animation.Interpolator;
import animator.component.util.icon.Svg;

public class EaseBothInterpolatorModel extends InterpolatorModel {

    public EaseBothInterpolatorModel() {
        setName("Ease Both");
        setIconSupplier(Svg.EASE_BOTH::node);
    }

    @Override
    public double curve(double t) {
        return clamp((t < 0.2) ? 3.125 * t * t
                : (t > 0.8) ? -3.125 * t * t + 6.25 * t - 2.125
                : 1.25 * t - 0.125);
    }

    @Override
    public Interpolator toFxInterpolator() {
        return Interpolator.EASE_BOTH;
    }
}
