package animator.component.util.text;

import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.util.Objects;

public class TextFieldHelper {

    public static <T> void bind(TextField textField, ObjectProperty<T> value, StringConverter<T> stringConverter, Runnable onCommit) {
        executeOnFocusLostOrEnter(textField, () -> {
            T oldValue = value.get();
            value.set(stringConverter.fromString(textField.getText()));
            textField.setText(stringConverter.toString(stringConverter.fromString(textField.getText())));
            textField.positionCaret(textField.getLength());
            if (onCommit != null && !Objects.equals(oldValue, value.get())) {
                onCommit.run();
            }
        });
        value.addListener((v, o, n) -> textField.setText(stringConverter.toString(n)));
        textField.setText(stringConverter.toString(value.get()));
    }

    public static void executeOnFocusLostOrEnter(TextField textField, Runnable runnable) {
        textField.focusedProperty().addListener((v, o, n) -> {
            if (!n) {
                runnable.run();
            }
        });
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                runnable.run();
            }
        });
    }
}
