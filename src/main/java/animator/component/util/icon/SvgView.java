package animator.component.util.icon;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.util.Locale;
import java.util.stream.Stream;

public class SvgView {

    private static final String STYLE_CLASS = "svg-view";
    private static final String PATH_STYLE_CLASS = "path";
    private static final String LAYER_STYLE_CLASS_INDEX_PREFIX = "layer-";

    private final SvgData svgData;

    private Pane root;

    public SvgView(SvgData svgData) {
        this.svgData = svgData;
    }

    public Pane getRoot() {
        if (root == null) {
            create();
        }
        return root;
    }

    private void create() {
        root = new Pane();
        addPathNodes();
        setStyleClasses();
        setSize();
    }

    private void addPathNodes() {
        Stream.of(svgData.getLayers()).map(this::createLayerNode).forEach(root.getChildren()::add);
    }

    private Node createLayerNode(SvgLayerData layer) {
        SVGPath pathNode = new SVGPath();
        if (layer.getPath() != null) {
            pathNode.setContent(layer.getPath());
        }
        if (layer.getFill() != null) {
            try {
                pathNode.setFill(Color.web(layer.getFill()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        pathNode.getStyleClass().add(PATH_STYLE_CLASS);
        return pathNode;
    }

    private void setStyleClasses() {
        root.getStyleClass().addAll(STYLE_CLASS, formatAsCssStyleClass(svgData.getName()));
        root.getChildren().forEach(child -> child.getStyleClass().add(LAYER_STYLE_CLASS_INDEX_PREFIX + (root.getChildren().indexOf(child) + 1)));
    }

    private String formatAsCssStyleClass(String name) {
        return name.toLowerCase(Locale.getDefault()).replaceAll(" ", "-").replaceAll("_", "-");
    }

    private void setSize() {
        root.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        if (svgData.getWidth() > 0) {
            root.setPrefWidth(svgData.getWidth());
        }
        if (svgData.getHeight() > 0) {
            root.setPrefHeight(svgData.getHeight());
        }
    }
}
