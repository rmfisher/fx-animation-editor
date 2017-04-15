package animator.component.property.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import animator.component.util.animation.FadeInAnimator;

public class TitleBarComponent extends HBox {

    private static final String TITLE_BAR_STYLE_CLASS = "title-bar";
    private static final String HIGHLIGHT_STYLE_CLASS = "highlight";
    private static final String RIGHT_LABEL_STYLE_CLASS = "right-label";

    private final Label label = new Label();
    private final Label rightLabel = new Label();
    private final BooleanProperty expanded = new SimpleBooleanProperty();
    private final BooleanProperty collapsible = new SimpleBooleanProperty(true);
    private final BooleanProperty animated = new SimpleBooleanProperty(true);

    public TitleBarComponent() {
        createUi();
        setOnMousePressed(this::onPressed);
    }

    public boolean isExpanded() {
        return expanded.get();
    }

    public BooleanProperty expandedProperty() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded.set(expanded);
    }

    public boolean isCollapsible() {
        return collapsible.get();
    }

    public BooleanProperty collapsibleProperty() {
        return collapsible;
    }

    public void setCollapsible(boolean collapsible) {
        this.collapsible.set(collapsible);
    }

    public String getText() {
        return label.getText();
    }

    public StringProperty textProperty() {
        return label.textProperty();
    }

    public void setText(String text) {
        label.setText(text);
    }

   public Label getRightLabel() {
        return rightLabel;
   }

    public boolean isAnimated() {
        return animated.get();
    }

    public BooleanProperty animatedProperty() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    private void createUi() {
        getStyleClass().add(TITLE_BAR_STYLE_CLASS);
        setAlignment(Pos.CENTER_RIGHT);
        setMaxWidth(Double.MAX_VALUE);

        Region selectedBar = new Region();
        selectedBar.getStyleClass().add(HIGHLIGHT_STYLE_CLASS);
        selectedBar.setMinWidth(Region.USE_PREF_SIZE);
        selectedBar.setMaxHeight(Region.USE_PREF_SIZE);
        selectedBar.setMaxWidth(Double.MAX_VALUE);
        StackPane.setAlignment(selectedBar, Pos.BOTTOM_CENTER);

        StackPane labelPane = new StackPane(label, selectedBar);
        labelPane.setMaxHeight(Double.MAX_VALUE);
        labelPane.setMaxWidth(Region.USE_PREF_SIZE);

        Region filler = new Region();
        HBox.setHgrow(filler, Priority.ALWAYS);
        rightLabel.getStyleClass().setAll(RIGHT_LABEL_STYLE_CLASS);

        new FadeInAnimator().apply(expanded, selectedBar);
        new FadeInAnimator().apply(expanded, rightLabel);

        getChildren().addAll(labelPane, filler, rightLabel);
    }

    private void onPressed(MouseEvent mouseEvent) {
        if (isCollapsible() && mouseEvent.getButton() == MouseButton.PRIMARY) {
            expanded.set(!expanded.get());
        }
    }
}
