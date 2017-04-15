package animator.persist.model.interpolator;

import java.util.List;

public class PInterpolatorListModel {

    private List<Double> splineParameters;
    private List<Double> overshootParameters;
    private List<Double> bounceParameters;

    public List<Double> getSplineParameters() {
        return splineParameters;
    }

    public void setSplineParameters(List<Double> splineParameters) {
        this.splineParameters = splineParameters;
    }

    public List<Double> getOvershootParameters() {
        return overshootParameters;
    }

    public void setOvershootParameters(List<Double> overshootParameters) {
        this.overshootParameters = overshootParameters;
    }

    public List<Double> getBounceParameters() {
        return bounceParameters;
    }

    public void setBounceParameters(List<Double> bounceParameters) {
        this.bounceParameters = bounceParameters;
    }
}
