package animator.component.util;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class FocusHelper {

    private static final PseudoClass FOCUS_SUPRESSED_PSEUDO_CLASS = PseudoClass.getPseudoClass("focus-suppressed");

    public static void requestFocusOnPress(Node node) {
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> node.requestFocus());
    }

    public static void redirectFocusOnPress(Node node, Node delegate) {
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> Platform.runLater(delegate::requestFocus));
    }

    public static void suppressFocusStyleOnPress(Node node) {
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> suppressFocusStyle(node));
        node.focusedProperty().addListener((v, o, n) -> endSuppressionIfFocusLost(node));
    }

    public static void suppressFocusStyleOnRelease(Node node) {
        node.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> suppressFocusStyle(node));
        node.focusedProperty().addListener((v, o, n) -> endSuppressionIfFocusLost(node));
    }

    private static void suppressFocusStyle(Node node) {
        if (!node.isFocused()) {
            node.pseudoClassStateChanged(FOCUS_SUPRESSED_PSEUDO_CLASS, true);
        }
    }

    private static void endSuppressionIfFocusLost(Node node) {
        if (!node.isFocused() && node.getScene() != null && node.getScene().getFocusOwner() != node) {
            node.pseudoClassStateChanged(FOCUS_SUPRESSED_PSEUDO_CLASS, false);
        }
    }
}
