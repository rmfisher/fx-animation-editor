package animator.component.scene.panning;

import animator.model.AnimatableField;
import animator.model.KeyValueModel;
import animator.model.SceneModel;
import animator.util.MathFunctions;

import javax.inject.Inject;
import java.util.Map;

public class PanningHelper {

    private final PanningComponent panningComponent;
    private final SceneModel sceneModel;

    @Inject
    public PanningHelper(PanningComponent panningComponent, SceneModel sceneModel) {
        this.panningComponent = panningComponent;
        this.sceneModel = sceneModel;
    }

    public void setInitialPosition(Map<AnimatableField, KeyValueModel> keyValues) {
        double centerX = panningComponent.getRoot().getWidth() / 2 - 40;
        double centerY = panningComponent.getRoot().getHeight() / 2 - 40;
        double startX = centerX - panningComponent.panXProperty().get();
        double startY = centerY - panningComponent.panYProperty().get();
        keyValues.get(AnimatableField.LAYOUT_X).setValue(snapToGrid(startX));
        keyValues.get(AnimatableField.LAYOUT_Y).setValue(snapToGrid(startY));
    }

    public void panToOrigin() {
        panningComponent.panXProperty().set(0);
        panningComponent.panYProperty().set(0);
    }

    public void panToContent() {
        sceneModel.getNodes().stream().map(n -> n.getNode().getBoundsInParent()).reduce(MathFunctions::union).ifPresent(bounds -> {
            double width = panningComponent.getRoot().getWidth();
            double height = panningComponent.getRoot().getHeight();
            double panX = bounds.getWidth() < width ? -bounds.getMinX() + (width - bounds.getWidth()) / 2 : -bounds.getMinX();
            double panY = bounds.getHeight() < height ? -bounds.getMinY() + (height - bounds.getHeight()) / 2 : -bounds.getMinY();
            panningComponent.panXProperty().set(Math.round(panX));
            panningComponent.panYProperty().set(Math.round(panY));
        });
    }

    private static double snapToGrid(double value) {
        return Math.round(value / 10) * 10;
    }
}
