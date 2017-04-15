package animator.component.util.widget;

import javafx.scene.layout.Region;

public class EightBoxes extends FourBoxes {

    private final Region top = new Region();
    private final Region right = new Region();
    private final Region bottom = new Region();
    private final Region left = new Region();

    public EightBoxes() {
        getChildren().addAll(top, right, bottom, left);
        top.getStyleClass().addAll("box", "top");
        right.getStyleClass().addAll("box", "right");
        bottom.getStyleClass().addAll("box", "bottom");
        left.getStyleClass().addAll("box", "left");
    }

    public Region getTop() {
        return top;
    }

    public Region getRight() {
        return right;
    }

    public Region getBottom() {
        return bottom;
    }

    public Region getLeft() {
        return left;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        double halfWidth = Math.round(widthProperty().get() / 2);
        double halfHeight = Math.round(heightProperty().get() / 2);

        top.setLayoutX(halfWidth - Math.round(top.getWidth() / 2));
        right.setLayoutX(widthProperty().get() - right.getWidth());
        right.setLayoutY(halfHeight - Math.round(right.getHeight() / 2));
        bottom.setLayoutX(halfWidth - Math.round(bottom.getWidth() / 2));
        bottom.setLayoutY(heightProperty().get() - bottom.getHeight());
        left.setLayoutY(halfHeight - Math.round(left.getHeight() / 2));
    }
}
