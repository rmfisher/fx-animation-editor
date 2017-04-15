package animator.command;

import animator.model.interpolator.InterpolationParameter;
import animator.model.interpolator.InterpolatorListModel;
import animator.model.interpolator.InterpolatorModel;

public class InterpolationParameterChangeCommand implements Command {

    private final InterpolatorListModel interpolators;
    private final InterpolatorModel selection;
    private final InterpolationParameter parameter;
    private final double newValue;
    private final double oldValue;

    public InterpolationParameterChangeCommand(InterpolatorListModel interpolators, InterpolationParameter parameter, double newValue, double oldValue) {
        this.interpolators = interpolators;
        this.selection = interpolators.getSelection();
        this.parameter = parameter;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    @Override
    public void execute() {
        interpolators.setSelection(selection);
        parameter.setValue(newValue);
    }

    @Override
    public void undo() {
        interpolators.setSelection(selection);
        parameter.setValue(oldValue);
    }
}
