package animator.component.property.color;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class ColorGraphic {

    private static final String STYLE_CLASS = "color-graphic";
    private static final String LINE_STYLE_CLASS = "line";
    private static final PseudoClass SHOW_BORDER_PSEUDO_CLASS = PseudoClass.getPseudoClass("show-border");

    private final Pane root = new Pane();
    private final Line line = new Line();
    private final ObjectProperty<Color> selection = new SimpleObjectProperty<>();

    private boolean showBorder;

    public ColorGraphic() {
        initUi();
        bindColors();
    }

    public ColorGraphic(Color color, boolean showBorder) {
        this();
        this.showBorder = showBorder;
        selection.set(color);
    }

    public Node getRoot() {
        return root;
    }

    public Color getSelection() {
        return selection.get();
    }

    public ObjectProperty<Color> selectionProperty() {
        return selection;
    }

    public void setSelection(Color color) {
        this.selection.set(color);
    }

    private void initUi() {
        root.getStyleClass().add(STYLE_CLASS);
        root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.getChildren().add(line);
        root.setPickOnBounds(true);

        Rectangle lineClip = new Rectangle();
        lineClip.widthProperty().bind(root.widthProperty());
        lineClip.heightProperty().bind(root.heightProperty());

        line.getStyleClass().add(LINE_STYLE_CLASS);
        line.endXProperty().bind(root.widthProperty());
        line.endYProperty().bind(root.heightProperty());
        line.setClip(lineClip);
        line.setManaged(false);
    }

    private void bindColors() {
        line.visibleProperty().bind(selection.isNull());
        selection.addListener((v, o, n) -> onSelectionChanged(n));
        onSelectionChanged(selection.get());
    }

    private void onSelectionChanged(Color newColor) {
        root.pseudoClassStateChanged(SHOW_BORDER_PSEUDO_CLASS, newColor == null || showBorder);
        root.setBackground(new Background(new BackgroundFill(newColor, null, null)));
    }
}
