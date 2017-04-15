package animator.component.property.color;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import animator.component.property.color.picker.ColorPickerComponent;
import animator.component.property.control.InterpolatorButtonComponent;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ColorEditorComponent {

    private static final String FXML_NAME = "ColorEditorComponent.fxml";
    private static final Color FX_ACCENT = Color.web("#0096C9");

    private final ColorGraphic fillGraphic = new ColorGraphic();
    private final ColorGraphic strokeGraphic = new ColorGraphic();
    private final InterpolatorButtonComponent interpolatorButton = new InterpolatorButtonComponent();
    private final List<ColorGraphic> standardColors = new ArrayList<>();
    private final ColorEditorPresenter presenter;
    private final ColorPickerComponent colorPicker;

    @FXML
    private VBox root;
    @FXML
    private HBox indicatorBox;
    @FXML
    private Label fillButton;
    @FXML
    private Label strokeButton;
    @FXML
    private HBox standardColorsBox;

    @Inject
    public ColorEditorComponent(ColorEditorPresenter presenter, ColorPickerComponent colorPicker) {
        this.presenter = presenter;
        this.colorPicker = colorPicker;
        loadFxml();
        initUi();
        initPresenter();
    }

    public Region getRoot() {
        return root;
    }

    public InterpolatorButtonComponent getInterpolatorButton() {
        return interpolatorButton;
    }

    public ColorEditorModel getModel() {
        return presenter.getModel();
    }

    Label getFillButton() {
        return fillButton;
    }

    Label getStrokeButton() {
        return strokeButton;
    }

    ColorGraphic getFillGraphic() {
        return fillGraphic;
    }

    ColorGraphic getStrokeGraphic() {
        return strokeGraphic;
    }

    ColorPickerComponent getColorPicker() {
        return colorPicker;
    }

    List<ColorGraphic> getStandardColors() {
        return standardColors;
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException ignored) {
        }
    }

    private void initUi() {
        fillButton.setGraphic(fillGraphic.getRoot());
        strokeButton.setGraphic(strokeGraphic.getRoot());
        indicatorBox.getChildren().add(interpolatorButton);

        root.getChildren().add(1, colorPicker.getRoot());

        addStandardColor(null, true);
        addStandardColor(Color.BLACK, false);
        addStandardColor(Color.WHITE, true);
        addStandardColor(FX_ACCENT, false);
    }

    private void addStandardColor(Color color, boolean showBorder) {
       ColorGraphic graphic = new ColorGraphic(color, showBorder);
       standardColors.add(graphic);
       standardColorsBox.getChildren().add(graphic.getRoot());
    }

    private void initPresenter() {
        presenter.setView(this);
    }
}
