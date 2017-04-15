package animator.model.interpolator;

import javafx.animation.Interpolator;
import animator.component.util.icon.Svg;

public class EaseOutInterpolatorModel extends InterpolatorModel {

    private static final double S1 = -25.0 / 9.0;
    private static final double S2 = 50.0 / 9.0;
    private static final double S3 = -16.0 / 9.0;
    private static final double S4 = 10.0 / 9.0;

    public EaseOutInterpolatorModel() {
        setName("Ease Out");
        setIconSupplier(Svg.EASE_OUT::node);
    }

    @Override
    public double curve(double t) {
        return clamp((t > 0.8) ? S1 * t * t + S2 * t + S3 : S4 * t);
    }

    @Override
    public Interpolator toFxInterpolator() {
        return Interpolator.EASE_OUT;
    }
}
