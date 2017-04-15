package animator.model;

import javafx.scene.effect.BlendMode;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class RectangleModel extends NodeModel {

    private Rectangle rectangle = new Rectangle();

    public RectangleModel() {
        rectangle.setStrokeType(StrokeType.INSIDE);
        rectangle.setBlendMode(BlendMode.MULTIPLY);
    }

    @Override
    public Rectangle getNode() {
        return rectangle;
    }
}
