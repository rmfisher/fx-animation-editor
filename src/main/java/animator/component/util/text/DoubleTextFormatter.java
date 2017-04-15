package animator.component.util.text;

import javafx.scene.control.TextFormatter;

public class DoubleTextFormatter extends TextFormatter<Double> {

    public DoubleTextFormatter() {
        super(change -> {
            if (!DoubleParser.canParse(change.getControlNewText())) {
                change.setText("");
            }
            change.setText(change.getText().replaceAll("[fdFD]", ""));
            return change;
        });
    }
}
