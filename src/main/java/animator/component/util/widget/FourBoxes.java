package animator.component.util.widget;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.layout.Region;

public class FourBoxes extends Parent {

    private final DoubleProperty width = newLayoutRequestingDoubleProperty();
    private final DoubleProperty height = newLayoutRequestingDoubleProperty();

    private final Region topLeft = new Region();
    private final Region topRight = new Region();
    private final Region bottomLeft = new Region();
    private final Region bottomRight = new Region();

    public FourBoxes() {
        getChildren().addAll(topLeft, topRight, bottomLeft, bottomRight);
        topLeft.getStyleClass().addAll("box", "top-left");
        topRight.getStyleClass().addAll("box","top-right");
        bottomLeft.getStyleClass().addAll("box","bottom-left");
        bottomRight.getStyleClass().addAll("box","bottom-right");
    }

    public Region getTopLeft() {
        return topLeft;
    }

    public Region getTopRight() {
        return topRight;
    }

    public Region getBottomLeft() {
        return bottomLeft;
    }

    public Region getBottomRight() {
        return bottomRight;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        topRight.setLayoutX(widthProperty().get() - topRight.getWidth());
        bottomLeft.setLayoutY(heightProperty().get() - bottomLeft.getHeight());
        bottomRight.setLayoutX(widthProperty().get() - bottomRight.getWidth());
        bottomRight.setLayoutY(heightProperty().get() - bottomRight.getHeight());
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    private DoubleProperty newLayoutRequestingDoubleProperty() {
        return new SimpleDoubleProperty() {

            @Override
            protected void invalidated() {
                super.invalidated();
                requestLayout();
            }
        };
    }
}
