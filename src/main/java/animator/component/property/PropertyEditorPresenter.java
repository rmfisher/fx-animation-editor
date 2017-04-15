package animator.component.property;

import com.google.common.eventbus.EventBus;
import javafx.beans.binding.Bindings;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import animator.component.property.control.DoubleInputComponent;
import animator.component.property.control.RotateComponent;
import animator.component.property.control.SliderComponent;
import animator.event.InterpolatorEditEvent;
import animator.event.MenuActionEvent;
import animator.event.PropertyEditEvent;
import animator.model.AnimatableField;
import animator.model.KeyValueModel;
import animator.model.NodeType;
import animator.model.PropertyModel;
import animator.util.ModelFunctions;
import animator.util.OsHelper;

import javax.inject.Inject;

import static animator.event.PropertyEditEvent.Type.*;

public class PropertyEditorPresenter {

    private final PropertyModel model;
    private final EventBus eventBus;
    private PropertyEditorComponent view;

    @Inject
    public PropertyEditorPresenter(PropertyModel model, EventBus eventBus) {
        this.model = model;
        this.eventBus = eventBus;
    }

    void setView(PropertyEditorComponent view) {
        this.view = view;
        bindViewAndModel();
        addUndoRedoFocusWorkaround();
    }

    private void bindViewAndModel() {
        view.getSnapToGridCheckBox().selectedProperty().bindBidirectional(model.snapToGridProperty());
        model.nodeTypeProperty().addListener((v, o, n) -> onNodeTypeChanged(n));
        onNodeTypeChanged(model.getNodeType());

        bindDouble(view.getLayoutXInput(), AnimatableField.LAYOUT_X);
        bindDouble(view.getLayoutYInput(), AnimatableField.LAYOUT_Y);
        bindDouble(view.getWidthInput(), AnimatableField.WIDTH);
        bindDouble(view.getHeightInput(), AnimatableField.HEIGHT);
        bindDouble(view.getStrokeWidthInput(), AnimatableField.STROKE_WIDTH);
        bindDouble(view.getArcWidthInput(), AnimatableField.ARC_WIDTH);
        bindDouble(view.getArcHeightInput(), AnimatableField.ARC_HEIGHT);
        bindDouble(view.getOpacityInput(), AnimatableField.OPACITY);
        bindDouble(view.getTranslateXInput(), AnimatableField.TRANSLATE_X);
        bindDouble(view.getTranslateYInput(), AnimatableField.TRANSLATE_Y);
        bindDouble(view.getTranslateZInput(), AnimatableField.TRANSLATE_Z);
        bindDouble(view.getRotateInput(), AnimatableField.ROTATE);
        bindDouble(view.getScaleXInput(), AnimatableField.SCALE_X);
        bindDouble(view.getScaleYInput(), AnimatableField.SCALE_Y);
        bindDouble(view.getScaleZInput(), AnimatableField.SCALE_Z);

        configureSlider(view.getOpacityInput(), AnimatableField.OPACITY);
        configureRotatorDial(view.getRotateInput(), AnimatableField.ROTATE);

        bindColors();
    }

    private void bindDouble(DoubleInputComponent input, AnimatableField field) {
        ModelFunctions.bindDoubleBidirectional(model.getProperties().get(field).valueProperty(), input.valueProperty());
        model.getProperties().get(field).interpolatorProperty().bindBidirectional(input.selectedInterpolatorProperty());
        input.setOnCommit(() -> eventBus.post(new PropertyEditEvent(SINGLE, field, input.getValue())));
        input.setOnInterpolatorSelected(() -> eventBus.post(new InterpolatorEditEvent(field, input.getSelectedInterpolator())));
        input.interpolatorButtonVisibleProperty().bind(model.nonZeroKeyFrameProperty());
        input.nullValueProperty().bind(Bindings.when(model.nonZeroKeyFrameProperty().not()).then(new Double(0)).otherwise((Double) null));
        input.clearButtonVisibleProperty().bind(model.nonZeroKeyFrameProperty());
    }

    private void configureSlider(SliderComponent slider, AnimatableField field) {
        slider.setOnChangeStarted(() -> eventBus.post(new PropertyEditEvent(START, field, null)));
        slider.setOnChange(() -> eventBus.post(new PropertyEditEvent(UPDATE, field, slider.getValue())));
        slider.setOnChangeFinished(() -> eventBus.post(new PropertyEditEvent(FINISH, field, slider.getValue())));
    }

    private void configureRotatorDial(RotateComponent rotateInput, AnimatableField field) {
        rotateInput.setOnChangeStarted(() -> eventBus.post(new PropertyEditEvent(START, field, null)));
        rotateInput.setOnChange(() -> eventBus.post(new PropertyEditEvent(UPDATE, field, rotateInput.getValue())));
        rotateInput.setOnChangeFinished(() -> eventBus.post(new PropertyEditEvent(FINISH, field, rotateInput.getValue())));
    }

    private void bindColors() {
        KeyValueModel fill = model.getProperties().get(AnimatableField.FILL);
        KeyValueModel stroke = model.getProperties().get(AnimatableField.STROKE);

        ModelFunctions.bindColorBidirectional(fill.valueProperty(), view.getColorEditor().getModel().fillProperty());
        ModelFunctions.bindColorBidirectional(stroke.valueProperty(), view.getColorEditor().getModel().strokeProperty());
        fill.interpolatorProperty().bindBidirectional(view.getColorEditor().getModel().fillInterpolatorProperty());
        stroke.interpolatorProperty().bindBidirectional(view.getColorEditor().getModel().strokeInterpolatorProperty());

        view.getColorEditor().getInterpolatorButton().visibleProperty().bind(model.nonZeroKeyFrameProperty());
        view.getColorEditor().getInterpolatorButton().managedProperty().bind(model.nonZeroKeyFrameProperty());
    }

    private void onNodeTypeChanged(NodeType nodeType) {
        String nodeTypeString = nodeType != null ? nodeType.toString() : "";
        view.getLayoutBox().getTitleBar().getRightLabel().setText(nodeTypeString);
        view.getColorBox().getTitleBar().getRightLabel().setText(nodeTypeString);
        view.getShapeBox().getTitleBar().getRightLabel().setText(nodeTypeString);
        view.getTransformBox().getTitleBar().getRightLabel().setText(nodeTypeString);

        view.getLayoutBox().setShowing(nodeType != null);
        view.getColorBox().setShowing(nodeType != null);
        view.getShapeBox().setShowing(nodeType != null);
        view.getTransformBox().setShowing(nodeType != null);

        if (nodeType != null) {
            view.getWidthInput().setVisible(nodeType == NodeType.RECTANGLE);
            view.getWidthInput().setManaged(nodeType == NodeType.RECTANGLE);
            view.getHeightInput().setVisible(nodeType == NodeType.RECTANGLE);
            view.getHeightInput().setManaged(nodeType == NodeType.RECTANGLE);
            view.getStrokeWidthInput().setVisible(nodeType == NodeType.RECTANGLE);
            view.getStrokeWidthInput().setManaged(nodeType == NodeType.RECTANGLE);
            view.getArcWidthInput().setVisible(nodeType == NodeType.RECTANGLE);
            view.getArcWidthInput().setManaged(nodeType == NodeType.RECTANGLE);
            view.getArcHeightInput().setVisible(nodeType == NodeType.RECTANGLE);
            view.getArcHeightInput().setManaged(nodeType == NodeType.RECTANGLE);
        }
    }

    private void addUndoRedoFocusWorkaround() {
        if (OsHelper.isWindows()) {
            // Need to apply event filter to override default undo/redo behavior of text fields.
            view.getRoot().addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        }
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.isShortcutDown() && event.getCode() == KeyCode.Z) {
            eventBus.post(MenuActionEvent.UNDO);
            event.consume();
        } else if (event.isShortcutDown() && event.getCode() == KeyCode.Y) {
            eventBus.post(MenuActionEvent.REDO);
            event.consume();
        }
    }
}
