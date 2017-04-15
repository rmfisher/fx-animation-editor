package animator.component.util.text;

import javafx.scene.control.TextFormatter;

public class NonNegativeDoubleTextFormatter extends TextFormatter<Double> {

    public NonNegativeDoubleTextFormatter() {
        super(change -> {

            if (!DoubleParser.canParse(change.getControlNewText()) || change.getControlNewText().contains("-")) {
                change.setText("");
            }
            change.setText(change.getText().replaceAll("[fdFD]", ""));
            return change;
        });
    }
}
