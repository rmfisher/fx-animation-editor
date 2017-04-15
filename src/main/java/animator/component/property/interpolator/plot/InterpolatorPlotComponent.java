package animator.component.property.interpolator.plot;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class InterpolatorPlotComponent {

    private static final int NUMBER_OF_X_VALUES = 200;
    private static final double PERCENT_GAP_X = 5;
    private static final double PERCENT_GAP_Y = 10;
    private static final double X_AXIS_HEIGHT = 3; // Couldn't quite manage to remove the x-axis entirely.

    private final DoubleProperty maxValue = new SimpleDoubleProperty();
    private final NumberAxis xAxis = new NumberAxis();
    private final InterpolatorYAxis yAxis = new InterpolatorYAxis();
    private final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
    private final List<XYChart.Data<Number, Number>> data;

    public InterpolatorPlotComponent() {
        xAxis.setAutoRanging(false);
        xAxis.setAnimated(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setTickLabelsVisible(false);
        xAxis.setLowerBound(0 - PERCENT_GAP_X / 100);
        xAxis.setUpperBound(1 + PERCENT_GAP_X / 100);

        yAxis.setAutoRanging(false);
        yAxis.setAnimated(false);
        yAxis.setLowerBound(0 - PERCENT_GAP_Y / 100);
        yAxis.setUpperBound(1 + PERCENT_GAP_Y / 100);

        chart.setLegendVisible(false);
        chart.setCreateSymbols(false);
        chart.setAnimated(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalGridLinesVisible(false);
        chart.setMinWidth(0);
        chart.setMinHeight(0);

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(chart.widthProperty());
        clip.heightProperty().bind(chart.heightProperty().subtract(X_AXIS_HEIGHT));
        chart.setClip(clip);

        data = createData();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().setAll(data);
        chart.getData().add(series);
    }

    public Region getRoot() {
        return chart;
    }

    public void plot(Function<Double, Double> function) {
        data.forEach(data -> data.setYValue(function.apply(data.getXValue().doubleValue())));
        double max = Math.max(data.stream().mapToDouble(data -> data.getYValue().doubleValue()).max().orElse(1.0), 1);
        double lowerBound = - max * (PERCENT_GAP_Y / 100);
        double upperBound = max * (1 + PERCENT_GAP_Y / 100);
        maxValue.set(max);
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
    }

    private List<XYChart.Data<Number, Number>> createData() {
        DoubleStream xValues = DoubleStream.iterate(0, n -> n + 1.0 / NUMBER_OF_X_VALUES).limit(NUMBER_OF_X_VALUES);
        return xValues.mapToObj(this::createDataPoint).collect(Collectors.toList());
    }

    private XYChart.Data<Number, Number> createDataPoint(double x) {
        XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>();
        dataPoint.setXValue(x);
        return dataPoint;
    }
}
