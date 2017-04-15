package animator.component.util.widget;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

import java.util.function.Function;

import static animator.component.util.binding.BindingFunctions.bind;

/**
 * Uses a VBox instead of a ListView inside its popup, so the popup is sized based on its content. The popup is right-aligned with the button.
 */
public class ComboButton<T> extends Label {

    private static final String POPUP_CONTENT_STYLE_CLASS = "combo-button-popup-content";
    private static final String CELL_STYLE_CLASS = "combo-button-cell";
    private static final PseudoClass SHOWING_PSEUDO_CLASS = PseudoClass.getPseudoClass("showing");
    private static final PseudoClass HOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("hover");
    private static final double POPUP_OFFSET_UNDER_BUTTON = 3;
    private static final double POPUP_OFFSET_OUTSIDE_SCREEN = 1.0e5;

    private final ListProperty<T> items = new SimpleListProperty<T>(FXCollections.observableArrayList());
    private final VBox popupContent = new VBox();
    private final Popup popup = new Popup();

    private Function<T, Node> cellFactory;

    public ComboButton() {
        createUi();
        bind(popupContent.getChildren(), items, this::createCell);
        setOnMousePressed(event -> onPressed());
    }

    public ObservableList<T> getItems() {
        return items;
    }

    public ListProperty<T> itemsProperty() {
        return items;
    }

    public void setItems(ObservableList<T> items) {
        this.items.set(items);
    }

    public void setCellFactory(Function<T, Node> cellFactory) {
        this.cellFactory = cellFactory;
    }

    protected Node createCell(T item) {
        Node cell;
        if (cellFactory != null) {
            cell = cellFactory.apply(item);
        } else {
            Label label = new Label(item != null ? item.toString() : "");
            label.setMaxWidth(Double.MAX_VALUE);
            cell = label;
        }
        cell.getStyleClass().add(CELL_STYLE_CLASS);
        return cell;
    }

    private void createUi() {
        setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        popupContent.getStyleClass().add(POPUP_CONTENT_STYLE_CLASS);
        popupContent.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> popup.hide());

        popup.setAutoHide(true);
        popup.getContent().add(popupContent);
        popup.showingProperty().addListener((v, o, n) -> pseudoClassStateChanged(SHOWING_PSEUDO_CLASS, n));
        popup.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_LEFT);
    }

    private void onPressed() {
        if (!popup.isShowing() && items.size() > 0) {
            Point2D buttonInScreen = localToScreen(0, 0);
            double x = Math.round(buttonInScreen.getX());
            double y = Math.round(buttonInScreen.getY());
            double popupWidth = popupContent.prefWidth(-1);
            if (popupWidth == 0) {
                // Hack to ensure popup position is correctly calculated before the popup is shown.
                popup.show(this, POPUP_OFFSET_OUTSIDE_SCREEN, POPUP_OFFSET_OUTSIDE_SCREEN);  // Probably not rendered but place off screen just in case.
                popup.hide();
                popupWidth = popupContent.prefWidth(-1);
            }
            popup.show(this, x - popupWidth + getWidth(), y + getHeight() + POPUP_OFFSET_UNDER_BUTTON);
            popupContent.getChildren().forEach(c -> c.pseudoClassStateChanged(HOVER_PSEUDO_CLASS, false));
        } else {
            popup.hide();
        }
    }
}
