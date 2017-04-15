package animator.model.interpolator;

import animator.component.util.icon.Svg;

public class BounceInterpolatorModel extends OvershootInterpolatorModel {

    public BounceInterpolatorModel() {
        setName("Bounce");
        setIconSupplier(Svg.BOUNCE::node);
        f0.setValue(2);
        f1.setValue(30);
        d1.setValue(30);
    }

    @Override
    public double curve(double time) {
        return toFxInterpolator().curve(time);
    }

    @Override
    public OvershootInterpolator toFxInterpolator() {
        return new BounceInterpolator(l.getValue(), f0.getValue(), f1.getValue(), d0.getValue(), d1.getValue(), stretch.getValue());
    }

    public class BounceInterpolator extends OvershootInterpolator {

        public BounceInterpolator(double l, double f0, double f1, double d0, double d1, double stretch) {
            super(l, f0, f1, d0, d1, stretch);
        }

        @Override
        protected double curve(double time) {
            double value = super.curve(time);
            if (value > 1) {
                return 2 - value;
            } else {
                return value;
            }
        }
    }
}
