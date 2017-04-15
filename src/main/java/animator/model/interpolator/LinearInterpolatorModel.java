package animator.model.interpolator;

import javafx.animation.Interpolator;
import animator.component.util.icon.Svg;

public class LinearInterpolatorModel extends InterpolatorModel {

    public LinearInterpolatorModel() {
        setName("Linear");
        setIconSupplier(Svg.LINEAR::node);
    }

    @Override
    public double curve(double t) {
        return t;
    }

    @Override
    public Interpolator toFxInterpolator() {
        return Interpolator.LINEAR;
    }
}
