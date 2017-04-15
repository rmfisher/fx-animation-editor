package animator.component.property.control;

import javafx.beans.DefaultProperty;
import javafx.beans.property.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import animator.component.util.animation.CollapseAnimator;

@DefaultProperty("content")
public class TitledBoxComponent extends VBox {

    private static final String LABEL_STYLE_CLASS = "no-node-selected-label";
    private static final String LABEL_TEXT = "No Selection";

    private final TitleBarComponent titleBar = new TitleBarComponent();
    private final StackPane outerStackPane = new StackPane();
    private final StackPane innerStackPane = new StackPane(); // This 2nd StackPane ensures the content stays put during the collapse animation.
    private final Label noElementSelectedLabel = new Label();
    private final ObjectProperty<Node> content = new SimpleObjectProperty<>();
    private final BooleanProperty showing = new SimpleBooleanProperty(true);
    private final CollapseAnimator collapseAnimator = new CollapseAnimator();

    public TitledBoxComponent() {
        initUi();
    }

    public Node getContent() {
        return content.get();
    }

    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public String getTitleText() {
        return titleBar.getText();
    }

    public StringProperty titleTextProperty() {
        return titleBar.textProperty();
    }

    public void setTitleText(String text) {
        titleBar.setText(text);
    }

    public boolean isExpanded() {
        return titleBar.isExpanded();
    }

    public BooleanProperty expandedProperty() {
        return titleBar.expandedProperty();
    }

    public void setExpanded(boolean expanded) {
        titleBar.setExpanded(expanded);
    }

    public boolean isCollapsible() {
        return titleBar.isCollapsible();
    }

    public BooleanProperty collapsibleProperty() {
        return titleBar.collapsibleProperty();
    }

    public void setCollapsible(boolean collapsible) {
        titleBar.setCollapsible(collapsible);
    }

    public boolean isAnimated() {
        return titleBar.isAnimated();
    }

    public BooleanProperty animatedProperty() {
        return titleBar.animatedProperty();
    }

    public void setAnimated(boolean animated) {
        titleBar.setAnimated(animated);
    }

    public boolean isShowing() {
        return showing.get();
    }

    public BooleanProperty showingProperty() {
        return showing;
    }

    public void setShowing(boolean showing) {
        this.showing.set(showing);
    }

    public TitleBarComponent getTitleBar() {
        return titleBar;
    }

    public void setOnCollapseFinished(Runnable runnable) {
        collapseAnimator.setOnFinished(runnable);
    }

    private void initUi() {
        super.getChildren().addAll(titleBar, outerStackPane);
        outerStackPane.getChildren().add(innerStackPane);
        innerStackPane.getChildren().add(noElementSelectedLabel);
        content.addListener((v, o, n) -> onContentChanged(o, n));

        outerStackPane.setMinHeight(0);
        outerStackPane.setAlignment(Pos.TOP_CENTER);
        innerStackPane.setMinHeight(Region.USE_PREF_SIZE);

        noElementSelectedLabel.visibleProperty().bind(showingProperty().not());
        noElementSelectedLabel.setText(LABEL_TEXT);
        noElementSelectedLabel.getStyleClass().setAll(LABEL_STYLE_CLASS);

        collapseAnimator.apply(titleBar.expandedProperty(), titleBar.animatedProperty(), outerStackPane);
    }

    private void onContentChanged(Node oldContent, Node newContent) {
        if (oldContent != null) {
            innerStackPane.getChildren().remove(oldContent);
            oldContent.visibleProperty().unbind();
        }
        if (newContent != null) {
            innerStackPane.getChildren().add(newContent);
            newContent.visibleProperty().bind(showingProperty());
        }
    }
}
