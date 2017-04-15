package animator.model.interpolator;

import java.util.Arrays;
import java.util.List;

public class DefaultInterpolators {

    public static final InterpolatorModel LINEAR = new LinearInterpolatorModel();
    public static final InterpolatorModel EASE_IN = new EaseInInterpolatorModel();
    public static final InterpolatorModel EASE_OUT = new EaseOutInterpolatorModel();
    public static final InterpolatorModel EASE_BOTH = new EaseBothInterpolatorModel();

    public static List<InterpolatorModel> getAll() {
        return Arrays.asList(LINEAR, EASE_IN, EASE_OUT, EASE_BOTH);
    }
}
