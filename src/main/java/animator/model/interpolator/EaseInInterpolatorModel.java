package animator.model.interpolator;

import javafx.animation.Interpolator;
import animator.component.util.icon.Svg;

public class EaseInInterpolatorModel extends InterpolatorModel {

    private static final double S1 = 25.0 / 9.0;
    private static final double S3 = 10.0 / 9.0;
    private static final double S4 = 1.0 / 9.0;

    public EaseInInterpolatorModel() {
        setName("Ease In");
        setIconSupplier(Svg.EASE_IN::node);
    }

    @Override
    public double curve(double t) {
        return clamp((t < 0.2) ? S1 * t * t : S3 * t - S4);
    }

    @Override
    public Interpolator toFxInterpolator() {
        return Interpolator.EASE_IN;
    }
}
