package animator.component.property.color.picker;

import javafx.scene.control.TextFormatter;

public class RgbTextFormatter extends TextFormatter<Integer> {

    public RgbTextFormatter() {
        super(change -> {
            if (change.isReplaced()) {
                if (change.getText().matches("[^0-9]")) {
                    change.setText(change.getControlText().substring(change.getRangeStart(), change.getRangeEnd()));
                }
            }
            if (change.isAdded()) {
                if (change.getText().matches("[^0-9]") || change.getControlNewText().length() > 3) {
                    change.setText("");
                }
            }
            return change;
        });
    }
}
