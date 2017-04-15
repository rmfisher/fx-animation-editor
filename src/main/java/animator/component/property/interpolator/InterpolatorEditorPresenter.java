package animator.component.property.interpolator;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import animator.command.CommandStack;
import animator.command.InterpolationParameterChangeCommand;
import animator.component.property.control.SliderComponent;
import animator.model.interpolator.*;

import javax.inject.Inject;

import static animator.component.util.binding.BindingFunctions.bind;

public class InterpolatorEditorPresenter {

    private final InterpolatorListModel model;
    private final CommandStack commandStack;
    private InterpolatorEditorComponent view;

    private boolean sliderConfigureInProgress;
    private double valueAtChangeStart;
    private double oldValueAtLastChange;

    @Inject
    public InterpolatorEditorPresenter(InterpolatorListModel model, CommandStack commandStack) {
        this.model = model;
        this.commandStack = commandStack;
    }

    void setView(InterpolatorEditorComponent view) {
        this.view = view;
        bindModelAndView();
        addSliderListeners();
        setDefaults();
    }

    ObservableList<InterpolatorModel> getInterpolators() {
        return model.getInterpolators();
    }

    private void bindModelAndView() {
        view.getInterpolatorComboBox().itemsProperty().bind(model.interpolatorsProperty());
        bind(view.getInterpolatorComboBox().getSelectionModel(), model.selectionProperty());
        model.selectionProperty().addListener((v, o, n) -> onSelectionChanged(o, n));
    }

    private void onSelectionChanged(InterpolatorModel oldValue, InterpolatorModel newValue) {
        if (oldValue != null) {
            unbindSliders(oldValue);
        }
        if (newValue != null) {
            configureSliders(newValue);
            updatePlot();
        } else {
            view.getSliders().forEach(s -> s.setVisible(false));
        }
    }

    private void unbindSliders(InterpolatorModel interpolator) {
        for (int i = 0; i < interpolator.getParameters().size(); i++) {
            view.getSliders().get(i).valueProperty().unbindBidirectional(interpolator.getParameters().get(i).valueAsObject());
        }
    }

    private void configureSliders(InterpolatorModel interpolator) {
        sliderConfigureInProgress = true;
        for (int i = 0; i < interpolator.getParameters().size(); i++) {
            configureSlider(interpolator.getParameters().get(i), view.getSliders().get(i));
        }
        for (int i = interpolator.getParameters().size(); i < view.getSliders().size(); i++) {
            view.getSliders().get(i).setVisible(false);
        }
        sliderConfigureInProgress = false;
    }

    private void configureSlider(InterpolationParameter parameter, SliderComponent slider) {
        slider.setVisible(true);
        slider.setLabelText(parameter.getName());
        // Need to reset slider values before setting new ones to avoid bound-value-cannot-be-set exception inside Slider class.
        slider.setValue(0.0);
        slider.setMax(Double.MAX_VALUE);
        slider.setMin(-Double.MAX_VALUE);
        slider.setValue(parameter.getValue());
        slider.setMax(parameter.getMax());
        slider.setMin(parameter.getMin());
        slider.setDecimalPlaces(parameter.getDecimalPlaces());
        slider.setBlockIncrement(parameter.getBlockIncrement());
        slider.setNullValue(parameter.getDefaultValue());
        slider.valueProperty().bindBidirectional(parameter.valueAsObject());
    }

    private void updatePlot() {
        if (model.getSelection() != null && !sliderConfigureInProgress) {
            view.getInterpolatorPlotComponent().plot(model.getSelection()::curve);
        }
    }

    private void addSliderListeners() {
        view.getSliders().forEach(s -> s.valueProperty().addListener((v, o, n) -> onSliderValueChanged(o)));
        view.getSliders().forEach(s -> s.setOnChangeStarted(() -> onChangeStarted(s)));
        view.getSliders().forEach(s -> s.setOnChangeFinished(() -> onChangeFinished(s, valueAtChangeStart)));
        view.getSliders().forEach(s -> s.setOnCommit(() -> onChangeFinished(s, oldValueAtLastChange)));
    }

    private void onSliderValueChanged(Double oldValue) {
        if (oldValue != null) {
            oldValueAtLastChange = oldValue;
        }
        Platform.runLater(this::updatePlot);
    }

    private void setDefaults() {
        model.getInterpolators().add(DefaultInterpolators.LINEAR);
        model.getInterpolators().add(DefaultInterpolators.EASE_IN);
        model.getInterpolators().add(DefaultInterpolators.EASE_OUT);
        model.getInterpolators().add(DefaultInterpolators.EASE_BOTH);
        model.getInterpolators().add(new SplineInterpolatorModel());
        model.getInterpolators().add(new OvershootInterpolatorModel());
        model.getInterpolators().add(new BounceInterpolatorModel());
        model.setSelection(model.getInterpolators().get(4));
    }

    private void onChangeStarted(SliderComponent slider) {
        InterpolationParameter parameter = getParameter(slider);
        valueAtChangeStart = Math.round(parameter.getValue() * 100) / 100.0;
    }

    private void onChangeFinished(SliderComponent slider, double oldValue) {
        InterpolationParameter parameter = getParameter(slider);
        double newValue = Math.round(parameter.getValue() * 100) / 100.0;
        if (Double.compare(oldValue, newValue) != 0) {
            commandStack.appendAndExecute(new InterpolationParameterChangeCommand(model, getParameter(slider), newValue, oldValue));
        }
    }

    private InterpolationParameter getParameter(SliderComponent slider) {
        return model.getSelection().getParameters().get(view.getSliders().indexOf(slider));
    }
}
