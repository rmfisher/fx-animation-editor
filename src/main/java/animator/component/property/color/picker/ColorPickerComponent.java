package animator.component.property.color.picker;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import javax.inject.Inject;

public class ColorPickerComponent {

    private static final String STYLE_CLASS = "color-picker";
    private static final String STYLESHEET = "color-picker-component.css";
    private static final String PALETTE_BOX_STYLE_CLASS = "palette-box";
    private static final String RGB_BOX_STYLE_CLASS = "rgb-box";
    private static final String SATURATION_AND_BRIGHTNESS_PANE_STYLE_CLASS = "saturation-and-brightness-pane";
    private static final String SATURATION_AND_BRIGHTNESS_INDICATOR_STYLE_CLASS = "saturation-and-brightness-indicator";
    private static final String HUE_PANE_STYLE_CLASS = "hue-pane";
    private static final String HUE_INDICATOR_STYLE_CLASS = "hue-indicator";
    private static final String TRANSPARENT_PATTERN_STYLE_CLASS = "transparent-pattern";
    private static final double BORDER_RADIUS = 3;

    private final ColorPickerPresenter presenter;
    private final HBox root = new HBox();
    private final VBox paletteBox = new VBox();
    private final VBox rgbBox = new VBox();
    private final Pane saturationAndBrightnessPane = new Pane();
    private final Pane huePane = new Pane();
    private final Region saturationAndBrightnessIndicator = new Region();
    private final Region hueIndicator = new Region();
    private final TextField redTextField = new TextField();
    private final TextField greenTextField = new TextField();
    private final TextField blueTextField = new TextField();
    private final TextField alphaTextField = new TextField();

    @Inject
    public ColorPickerComponent(ColorPickerPresenter presenter) {
        this.presenter = presenter;
        initUi();
        initPresenter(presenter);
    }

    public Region getRoot() {
        return root;
    }

    public Color getColor() {
        return presenter.colorProperty().get();
    }

    public ObjectProperty<Color> colorProperty() {
        return presenter.colorProperty();
    }

    public void setColor(Color color) {
        presenter.colorProperty().set(color);
    }

    public void setOnChangeStarted(Runnable onChangeStarted) {
        presenter.setOnChangeStarted(onChangeStarted);
    }

    public void setOnChanged(Runnable onChanged) {
        presenter.setOnChanged(onChanged);
    }

    public void setOnChangeFinished(Runnable onChangeFinished) {
        presenter.setOnChangeFinished(onChangeFinished);
    }

    public void setOnCommit(Runnable onCommit) {
        presenter.setOnCommit(onCommit);
    }

    Region getSaturationAndBrightnessPane() {
        return saturationAndBrightnessPane;
    }

    Region getHuePane() {
        return huePane;
    }

    Region getSaturationAndBrightnessIndicator() {
        return saturationAndBrightnessIndicator;
    }

    Region getHueIndicator() {
        return hueIndicator;
    }

    TextField getRedTextField() {
        return redTextField;
    }

    TextField getGreenTextField() {
        return greenTextField;
    }

    TextField getBlueTextField() {
        return blueTextField;
    }

    TextField getAlphaTextField() {
        return alphaTextField;
    }

    private void initUi() {
        root.getStyleClass().addAll(STYLE_CLASS);
        root.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
        paletteBox.getStyleClass().add(PALETTE_BOX_STYLE_CLASS);
        paletteBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(paletteBox, Priority.ALWAYS);
        rgbBox.getStyleClass().add(RGB_BOX_STYLE_CLASS);

        saturationAndBrightnessPane.getStyleClass().add(SATURATION_AND_BRIGHTNESS_PANE_STYLE_CLASS);
        saturationAndBrightnessIndicator.getStyleClass().add(SATURATION_AND_BRIGHTNESS_INDICATOR_STYLE_CLASS);
        huePane.getStyleClass().add(HUE_PANE_STYLE_CLASS);
        huePane.setBackground(new Background(new BackgroundFill(createHueGradient(), null, null)));
        hueIndicator.getStyleClass().add(HUE_INDICATOR_STYLE_CLASS);

        StackPane saturationAndBrightnessContainer = addAndConfigure(saturationAndBrightnessPane, saturationAndBrightnessIndicator);
        Region transparentPattern = new Region();
        transparentPattern.getStyleClass().add(TRANSPARENT_PATTERN_STYLE_CLASS);
        saturationAndBrightnessContainer.getChildren().add(0, transparentPattern);
        saturationAndBrightnessContainer.setMinHeight(0);
        saturationAndBrightnessContainer.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(saturationAndBrightnessContainer, Priority.ALWAYS);
        applyClip(saturationAndBrightnessPane);
        applyClip(transparentPattern);

        addAndConfigure(huePane, hueIndicator);
        applyClip(huePane);

        addWithLabelOverlaid("R", redTextField);
        addWithLabelOverlaid("G", greenTextField);
        addWithLabelOverlaid("B", blueTextField);
        addWithLabelOverlaid("A", alphaTextField);

        redTextField.setContextMenu(new ContextMenu());
        greenTextField.setContextMenu(new ContextMenu());
        blueTextField.setContextMenu(new ContextMenu());
        alphaTextField.setContextMenu(new ContextMenu());

        root.getChildren().addAll(paletteBox, rgbBox);
    }

    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int x = 0; x < 255; x++) {
            offset = 1.0 / 255 * x;
            int h = (int) ((x / 255.0) * 360);
            stops[x] = new Stop(offset, Color.hsb(h, 1, 1));
        }
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
    }

    private StackPane addAndConfigure(Pane pane, Region indicator) {
        StackPane container = new StackPane(pane, indicator);
        indicator.prefWidthProperty().addListener((v, o, n) -> indicator.autosize());
        indicator.prefHeightProperty().addListener((v, o, n) -> indicator.autosize());
        indicator.setManaged(false);
        indicator.setMouseTransparent(true);
        indicator.setCache(true);

        paletteBox.getChildren().add(container);
        return container;
    }

    private void applyClip(Region region) {
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(region.widthProperty());
        clip.heightProperty().bind(region.heightProperty());
        clip.setArcWidth(2 * BORDER_RADIUS);
        clip.setArcHeight(2 * BORDER_RADIUS);
        region.setClip(clip);
    }

    private void addWithLabelOverlaid(String labelText, TextField textField) {
        Label label = new Label(labelText);
        label.setMouseTransparent(true);
        StackPane.setAlignment(label, Pos.CENTER_LEFT);
        StackPane container = new StackPane(textField, label);
        rgbBox.getChildren().add(container);
    }

    private void initPresenter(ColorPickerPresenter presenter) {
        presenter.setView(this);
    }
}