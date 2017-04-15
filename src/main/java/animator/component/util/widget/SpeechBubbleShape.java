package animator.component.util.widget;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;
import javafx.scene.shape.*;

public class SpeechBubbleShape {

    private final Path path = new Path();
    private final DoubleProperty width = new SimpleDoubleProperty();
    private final DoubleProperty height = new SimpleDoubleProperty();
    private final DoubleProperty anchorBreadth = new SimpleDoubleProperty(8);
    private final DoubleProperty anchorDepth = new SimpleDoubleProperty(5);

    public SpeechBubbleShape() {
        createPath();
    }

    public void applyTo(Region region) {
        region.setShape(path);
        width.bind(region.widthProperty());
        height.bind(region.heightProperty());
    }

    public double getAnchorBreadth() {
        return anchorBreadth.get();
    }

    public DoubleProperty anchorBreadthProperty() {
        return anchorBreadth;
    }

    public void setAnchorBreadth(double anchorBreadth) {
        this.anchorBreadth.set(anchorBreadth);
    }

    public double getAnchorDepth() {
        return anchorDepth.get();
    }

    public DoubleProperty anchorDepthProperty() {
        return anchorDepth;
    }

    public void setAnchorDepth(double anchorDepth) {
        this.anchorDepth.set(anchorDepth);
    }

    private void createPath() {
        MoveTo start = new MoveTo();
        start.xProperty().bind(anchorDepth);

        HLineTo top = new HLineTo();
        top.xProperty().bind(width);

        VLineTo right = new VLineTo();
        right.yProperty().bind(height);

        HLineTo bottom = new HLineTo();
        bottom.xProperty().bind(anchorDepth);

        VLineTo lowerLeft = new VLineTo();
        lowerLeft.yProperty().bind(height.add(anchorBreadth).divide(2));

        LineTo lowerAnchor = new LineTo();
        lowerAnchor.yProperty().bind(height.divide(2));

        LineTo upperAnchor = new LineTo();
        upperAnchor.xProperty().bind(anchorDepth);
        upperAnchor.yProperty().bind(height.subtract(anchorBreadth).divide(2));

        path.getElements().addAll(start, top, right, bottom, lowerLeft, lowerAnchor, upperAnchor, new ClosePath());
    }
}
