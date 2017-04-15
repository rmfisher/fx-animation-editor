package animator.component.scene.panning;

import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PanningPresenter {

    private PanningComponent view;

    private double contentXAtPress;
    private double contentYAtPress;
    private double screenXAtPress;
    private double screenYAtPress;
    private boolean panningActive;

    void setView(PanningComponent view) {
        this.view = view;
        initDragHandlers();
    }

    void centerContent() {
        Bounds contentLayoutBounds = view.getContent().getLayoutBounds();
        double contentWidth = contentLayoutBounds.getWidth();
        double contentHeight = contentLayoutBounds.getHeight();
        double rootWidth = view.getRoot().getLayoutBounds().getWidth();
        double rootHeight = view.getRoot().getLayoutBounds().getHeight();
        double contentStartX = Math.round((rootWidth - contentWidth) / 2);
        double contentStartY = Math.round((rootHeight - contentHeight) / 2);
        view.getContent().setLayoutX(contentStartX - contentLayoutBounds.getMinX());
        view.getContent().setLayoutY(contentStartY - contentLayoutBounds.getMinY());
    }

    private void initDragHandlers() {
        view.getRoot().addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        view.getRoot().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);
        view.getRoot().addEventHandler(MouseEvent.MOUSE_RELEASED, this::onMouseReleased);
    }

    private void onMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            contentXAtPress = view.getContent().getLayoutX();
            contentYAtPress = view.getContent().getLayoutY();
            screenXAtPress = event.getScreenX();
            screenYAtPress = event.getScreenY();
            setSceneCursor(Cursor.MOVE);
            panningActive = true;
        }
    }

    private void onMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY && panningActive) {
            double deltaX = event.getScreenX() - screenXAtPress;
            double deltaY = event.getScreenY() - screenYAtPress;
            view.getContent().setLayoutX(contentXAtPress + deltaX);
            view.getContent().setLayoutY(contentYAtPress + deltaY);
        }
    }

    private void onMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            panningActive = false;
            setSceneCursor(null);
        }
    }

    private void setSceneCursor(Cursor cursor) {
        if (view.getRoot().getScene() != null) {
            view.getRoot().getScene().setCursor(cursor);
        }
    }
}
