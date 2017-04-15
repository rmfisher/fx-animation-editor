package animator.model.interpolator;

import javafx.animation.Interpolator;
import animator.component.util.icon.Svg;

public class OvershootInterpolatorModel extends InterpolatorModel {

    protected final InterpolationParameter l = new InterpolationParameter("Linearity", 0.01, 0.3, 2, 0.01, 0.1);
    protected final InterpolationParameter f0 = new InterpolationParameter("Init. Frequency", 0.1, 40, 1, 0.1, 10);
    protected final InterpolationParameter f1 = new InterpolationParameter("Freq. Growth", 0, 40, 1, 0.1, 0);
    protected final InterpolationParameter d0 = new InterpolationParameter("Init. Damping", 0, 40, 1, 0.1, 10);
    protected final InterpolationParameter d1 = new InterpolationParameter("Damp. Growth", 0, 80, 1, 0.1, 20);
    protected final InterpolationParameter stretch = new InterpolationParameter("Stretch", 1, 10, 2, 0.1, 2);

    public OvershootInterpolatorModel() {
        setName("Overshoot");
        setIconSupplier(Svg.OVERSHOOT::node);
        getParameters().addAll(l, f0, f1, d0, d1, stretch);
    }

    @Override
    public double curve(double actualTime) {
        return toFxInterpolator().curve(actualTime);
    }

    @Override
    public OvershootInterpolator toFxInterpolator() {
        return new OvershootInterpolator(l.getValue(), f0.getValue(), f1.getValue(), d0.getValue(), d1.getValue(), stretch.getValue());
    }

    public class OvershootInterpolator extends Interpolator {

        private final double l;
        private final double f0;
        private final double f1;
        private final double d0;
        private final double d1;
        private final double stretch;

        public OvershootInterpolator(double l, double f0, double f1, double d0, double d1, double stretch) {
            this.l = l;
            this.f0 = f0;
            this.f1 = f1;
            this.d0 = d0;
            this.d1 = d1;
            this.stretch = stretch;
        }

        @Override
        protected double curve(double actualTime) {
            double t = actualTime / stretch;
            double f = f0 + t * f1;
            double d = d0 + t * d1;

            if (t < l && l > 0) {
                return t / l;
            } else {
                double a = 1.0 / l;
                double w = f * Math.PI * 2;
                return 1 + a * (Math.sin((t - l) * w) / Math.exp(d * (t - l)) / w);
            }
        }
    }
}
