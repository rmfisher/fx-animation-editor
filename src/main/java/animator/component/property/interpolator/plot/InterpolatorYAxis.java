package animator.component.property.interpolator.plot;

import javafx.scene.chart.ValueAxis;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class InterpolatorYAxis extends ValueAxis<Number> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0E0");
    private static final double PIXEL_CUTOFF = 8;

    private boolean axisLayoutInProgress;

    @Override
    public double getDisplayPosition(Number value) {
        // Hack to ensure that the tick marks are snapped to pixel, but the series y-values are not distorted.
        if (axisLayoutInProgress) {
            return Math.round(super.getDisplayPosition(value));
        } else {
            return super.getDisplayPosition(value);
        }
    }

    @Override
    protected List<Number> calculateMinorTickMarks() {
        return new ArrayList<>();
    }

    @Override
    protected void setRange(Object range, boolean animate) {
        if (range == null) {
            return;
        }
        Object[] r = (Object[]) range;
        Number lower = (Number) r[0];
        Number upper = (Number) r[1];
        setLowerBound(lower.doubleValue());
        setUpperBound(upper.doubleValue());
    }

    @Override
    protected Object getRange() {
        return new Object[]{getLowerBound(), getUpperBound()};
    }

    @Override
    protected List<Number> calculateTickValues(double length, Object range) {
        if (range == null) {
            return new ArrayList<>();
        }
        Object[] r = (Object[]) range;
        Number lower = (Number) r[0];
        Number upper = (Number) r[1];
        double tickSeparation = calculateTickSeparation(upper.doubleValue() - lower.doubleValue());
        double currentTick = Math.floor(lower.doubleValue() / tickSeparation) * tickSeparation;

        List<Number> tickValues = new ArrayList<>();
        if (isInRange(currentTick, lower.doubleValue(), upper.doubleValue())) {
            tickValues.add(currentTick);
        }
        while (currentTick <= upper.doubleValue()) {
            currentTick += tickSeparation;
            if (isInRange(currentTick, lower.doubleValue(), upper.doubleValue())) {
                tickValues.add(currentTick);
            }
        }
        return tickValues;
    }

    @Override
    protected String getTickMarkLabel(Number value) {
        return new DecimalFormat().format(value.doubleValue());
    }

    @Override
    protected void layoutChildren() {
        axisLayoutInProgress = true;
        super.layoutChildren();
        axisLayoutInProgress = false;
    }

    private double calculateTickSeparation(double range) {
        double roughValue = range / 3; // Try for 3-ish ticks.
        try {
            String roughValueFormattedTo1SF = DECIMAL_FORMAT.format(roughValue);
            int firstDigit = Integer.parseInt(roughValueFormattedTo1SF.substring(0, 1));

            // Round to nearest of either 1, 2, or 5:
            int roundedFirstDigit;
            if (firstDigit == 3) {
                roundedFirstDigit = 2;
            } else if (firstDigit > 3) {
                roundedFirstDigit = 5;
            } else {
                roundedFirstDigit = firstDigit;
            }
            String roundedValueFormattedTo1SF = String.valueOf(roundedFirstDigit) + roughValueFormattedTo1SF.substring(1);
            return DECIMAL_FORMAT.parse(roundedValueFormattedTo1SF).doubleValue();
        } catch (NumberFormatException | ParseException ignored) {
            return roughValue;
        }
    }

    private boolean isInRange(double current, double lower, double upper) {
        double lowerPos = getDisplayPosition(lower);
        double upperPos = getDisplayPosition(upper);
        double currentPos = getDisplayPosition(current);
        return Math.abs(currentPos - lowerPos) > PIXEL_CUTOFF && Math.abs(currentPos - upperPos) > PIXEL_CUTOFF;
    }
}
