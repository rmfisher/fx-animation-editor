package animator.component.property.color;

import com.google.common.eventbus.EventBus;
import javafx.css.PseudoClass;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import animator.event.InterpolatorEditEvent;
import animator.event.PropertyEditEvent;
import animator.model.AnimatableField;

import javax.inject.Inject;
import java.util.Objects;

import static animator.event.PropertyEditEvent.Type.*;

public class ColorEditorPresenter {

    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    private final ColorEditorModel model;
    private final EventBus eventBus;
    private ColorEditorComponent view;

    @Inject
    public ColorEditorPresenter(ColorEditorModel model, EventBus eventBus) {
        this.model = model;
        this.eventBus = eventBus;
    }

    void setView(ColorEditorComponent view) {
        this.view = view;
        bindModelAndView();
        addActionHandlers();
    }

    ColorEditorModel getModel() {
        return model;
    }

    private void bindModelAndView() {
        view.getFillGraphic().selectionProperty().bind(model.fillProperty());
        view.getStrokeGraphic().selectionProperty().bind(model.strokeProperty());
        model.strokeSelectedProperty().addListener((v, o, n) -> onStrokeSelectedChanged(n));
        onStrokeSelectedChanged(model.isStrokeSelected());
    }

    private void onStrokeSelectedChanged(boolean newValue) {
        view.getStrokeButton().pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, newValue);
        view.getFillButton().pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, !newValue);
        if (newValue) {
            view.getColorPicker().colorProperty().unbindBidirectional(model.fillProperty());
            view.getInterpolatorButton().selectionProperty().unbindBidirectional(model.fillInterpolatorProperty());
            view.getColorPicker().setColor(model.getStroke());
            view.getInterpolatorButton().setSelection(model.getStrokeInterpolator());
            model.strokeProperty().bindBidirectional(view.getColorPicker().colorProperty());
            model.strokeInterpolatorProperty().bindBidirectional(view.getInterpolatorButton().selectionProperty());
        } else {
            view.getColorPicker().colorProperty().unbindBidirectional(model.strokeProperty());
            view.getInterpolatorButton().selectionProperty().unbindBidirectional(model.strokeInterpolatorProperty());
            view.getColorPicker().setColor(model.getFill());
            view.getInterpolatorButton().setSelection(model.getFillInterpolator());
            view.getColorPicker().colorProperty().bindBidirectional(model.fillProperty());
            view.getInterpolatorButton().selectionProperty().bindBidirectional(model.fillInterpolatorProperty());
        }
    }

    private void addActionHandlers() {
        view.getStrokeButton().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> model.setStrokeSelected(true));
        view.getFillButton().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> model.setStrokeSelected(false));
        view.getStandardColors().forEach(graphic -> graphic.getRoot().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onColorSelected(graphic)));
        view.getColorPicker().setOnChangeStarted(() -> postChangeEvent(START));
        view.getColorPicker().setOnChanged(() -> postChangeEvent(UPDATE));
        view.getColorPicker().setOnChangeFinished(() -> postChangeEvent(FINISH));
        view.getColorPicker().setOnCommit(() -> postChangeEvent(SINGLE));
        view.getInterpolatorButton().setOnItemSelected(this::postInterpolatorChangeEvent);
    }

    private void onColorSelected(ColorGraphic graphic) {
        Color oldValue = view.getColorPicker().getColor();
        view.getColorPicker().setColor(graphic.getSelection());
        if (!Objects.equals(oldValue, graphic.getSelection())) {
            postChangeEvent(SINGLE);
        }
    }

    private void postChangeEvent(PropertyEditEvent.Type type) {
        Color value = type == START ? null : view.getColorPicker().getColor();
        AnimatableField field = model.isStrokeSelected() ? AnimatableField.STROKE : AnimatableField.FILL;
        eventBus.post(new PropertyEditEvent(type, field, value));
    }

    private void postInterpolatorChangeEvent() {
        if (model.isStrokeSelected()) {
            eventBus.post(new InterpolatorEditEvent(AnimatableField.STROKE, model.getStrokeInterpolator()));
        } else {
            eventBus.post(new InterpolatorEditEvent(AnimatableField.FILL, model.getFillInterpolator()));
        }
    }
}
