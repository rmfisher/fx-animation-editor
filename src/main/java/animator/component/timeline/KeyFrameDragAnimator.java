package animator.component.timeline;

import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyFrameDragAnimator {

    private static final Duration DURATION = Duration.millis(150);
    private static final double DRAG_INERTIA = 3;

    private HBox container;
    private BiConsumer<Integer, Integer> repositionHandler;
    private boolean dragActive;
    private boolean inertiaOvercome;
    private double nodeXOnPress;
    private double mouseXOnPress;
    private double minWidthBackup;
    private double prefWidthBackup;
    private double maxWidthBackup;
    private Map<Node, TranslateAnimation> translates;

    public void initialize(HBox container, BiConsumer<Integer, Integer> repositionHandler) {
        this.container = container;
        this.repositionHandler = repositionHandler;
    }

    public void addHandlers(Node child) {
        child.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onPressed(child, event));
        child.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> onDragged(child, event));
        child.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> onReleased(child, event));
    }

    private void onPressed(Node dragged, MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            dragActive = true;
            inertiaOvercome = false;
            nodeXOnPress = dragged.getLayoutX();
            mouseXOnPress = event.getScreenX();

            minWidthBackup = container.getMinWidth();
            prefWidthBackup = container.getPrefWidth();
            maxWidthBackup = container.getMaxWidth();
            container.setMinWidth(container.getWidth());
            container.setPrefWidth(container.getWidth());
            container.setMaxWidth(container.getWidth());

            translates = moveableChildren().collect(Collectors.toMap(Function.identity(), child -> new TranslateAnimation(child, dragged)));
            moveableChildren().forEach(child -> child.setManaged(false));

            // Move dragged node temporarily to front. Reset to original index on mouse release.
            dragged.toFront();
        }
    }

    private void onDragged(Node dragged, MouseEvent event) {
        if (!inertiaOvercome) {
            inertiaOvercome = Math.abs(event.getScreenX() - mouseXOnPress) > DRAG_INERTIA;
        }
        if (event.getButton() == MouseButton.PRIMARY && dragActive && inertiaOvercome) {
            dragged.setLayoutX(clamp(nodeXOnPress + event.getScreenX() - mouseXOnPress, dragged));
            moveableChildren().filter(child -> child != dragged).forEach(child -> updatePosition(child, dragged));
        }
    }

    private void onReleased(Node dragged, MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && dragActive) {
            dragActive = false;

            container.setMinWidth(minWidthBackup);
            container.setPrefWidth(prefWidthBackup);
            container.setMaxWidth(maxWidthBackup);

            int draggedIndex = translates.get(dragged).initialIndex;
            int dropIndex = translates.values().stream()
                    .filter(t -> t.forward.get())
                    .map(t -> t.initialIndex)
                    .map(i -> i < draggedIndex ? i : i - 1)
                    .min(Comparator.naturalOrder())
                    .orElse(container.getChildren().size() - 1);

            // Reset dragged node to original index.
            for (int i = draggedIndex; i < container.getChildren().size() - 1; i++) {
                container.getChildren().get(draggedIndex).toFront();
            }

            moveableChildren().forEach(child -> child.setManaged(true));
            moveableChildren().forEach(child -> child.setTranslateX(0));
            translates.values().forEach(animation -> {
                if (animation.translate != null) {
                    animation.translate.stop();
                    animation.translate.setNode(null);
                }
            });
            translates = null;

            if (repositionHandler != null && draggedIndex != dropIndex) {
                repositionHandler.accept(draggedIndex, dropIndex);
            }
        }
    }

    private Stream<Node> moveableChildren() {
        return container.getChildren().stream().filter(child -> container.getChildren().indexOf(child) > 0);
    }

    private double clamp(double value, Node draggedNode) {
        double min = container.getChildren().get(0).getLayoutBounds().getWidth() + container.getSpacing();
        double max = container.getWidth() - draggedNode.getLayoutBounds().getWidth();
        return Math.max(min, Math.min(max, value));
    }

    private void updatePosition(Node child, Node dragged) {
        double childMidX = translates.get(child).initialX + child.getLayoutBounds().getWidth() / 2;
        TranslateAnimation animation = translates.get(child);
        if (animation.initialIndex < translates.get(dragged).initialIndex) {
            animation.forward.set(dragged.getLayoutX() < childMidX);
        } else {
            animation.forward.set(childMidX > dragged.getLayoutX() + dragged.getLayoutBounds().getWidth());
        }
    }

    private class TranslateAnimation {

        final double initialX;
        final int initialIndex;
        final TranslateTransition translate;
        final BooleanProperty forward = new SimpleBooleanProperty();

        public TranslateAnimation(Node child, Node dragged) {
            initialX = child.getLayoutX();
            initialIndex = container.getChildren().indexOf(child);
            if (child != dragged) {
                translate = new TranslateTransition(DURATION, child);

                double displacement = dragged.getLayoutBounds().getWidth() + container.getSpacing();
                if (initialIndex < container.getChildren().indexOf(dragged)) {
                    translate.setFromX(0);
                    translate.setToX(displacement);
                } else {
                    translate.setFromX(-displacement);
                    translate.setToX(0);
                    translate.jumpTo(DURATION);
                    forward.set(true);
                }

                forward.addListener((v, o, n) -> {
                    translate.setRate(n ? 1 : -1);
                    translate.play();
                });
            } else {
                translate = null;
            }
        }
    }
}
