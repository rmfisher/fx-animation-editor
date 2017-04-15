package animator.command;

import animator.event.TimelineSceneSynchronizer;
import animator.model.RectangleModel;
import animator.model.SceneModel;
import animator.model.TimelineModel;

public class AddRectangleCommand extends AddNodeCommand {

    private final RectangleModel rectangleModel;
    private final SceneModel sceneModel;

    public AddRectangleCommand(RectangleModel rectangleModel, SceneModel sceneModel, TimelineModel timelineModel, TimelineSceneSynchronizer synchronizer) {
        super(rectangleModel, timelineModel, synchronizer);
        this.rectangleModel = rectangleModel;
        this.sceneModel = sceneModel;
    }

    @Override
    public void execute() {
        addKeyValues();
        sceneModel.getSelectedNodes().forEach(n -> n.setSelected(false));
        rectangleModel.setSelected(true);
        sceneModel.getNodes().add(rectangleModel);
    }

    @Override
    public void undo() {
        rectangleModel.setSelected(false);
        sceneModel.getNodes().remove(rectangleModel);
        removeKeyValues();
    }
}