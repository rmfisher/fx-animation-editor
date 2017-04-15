package animator.model.interpolator;

import javafx.animation.Interpolator;
import animator.component.util.icon.Svg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SplineInterpolatorModel extends InterpolatorModel {

    private InterpolationParameter x1 = new InterpolationParameter("Control Point X\u2081", 0, 1, 2, 0.01, 0.75);
    private InterpolationParameter y1 = new InterpolationParameter("Control Point Y\u2081", 0, 1, 2, 0.01, 0.25);
    private InterpolationParameter x2 = new InterpolationParameter("Control Point X\u2082", 0, 1, 2, 0.01, 0.25);
    private InterpolationParameter y2 = new InterpolationParameter("Control Point Y\u2082", 0, 1, 2, 0.01, 0.75);
    private Interpolator fxInterpolator;

    public SplineInterpolatorModel() {
        setName("Spline");
        setIconSupplier(Svg.SPLINE::node);
        x1.valueProperty().addListener((v, o, n) -> configure());
        y1.valueProperty().addListener((v, o, n) -> configure());
        x2.valueProperty().addListener((v, o, n) -> configure());
        y2.valueProperty().addListener((v, o, n) -> configure());
        configure();
        getParameters().addAll(x1, y1, x2, y2);
    }

    @Override
    public double curve(double x) {
        try {
            Method m = fxInterpolator.getClass().getDeclaredMethod("curve", double.class);
            m.setAccessible(true);
            return (double) m.invoke(fxInterpolator, x);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return x;
        }
    }

    @Override
    public Interpolator toFxInterpolator() {
        return Interpolator.SPLINE(x1.getValue(), y1.getValue(), x2.getValue(), y2.getValue());
    }

    private void configure() {
        fxInterpolator = Interpolator.SPLINE(x1.getValue(), y1.getValue(), x2.getValue(), y2.getValue());
    }
}
