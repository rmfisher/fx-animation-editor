package animator.component.property;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import animator.component.property.color.ColorEditorComponent;
import animator.component.property.control.DoubleInputComponent;
import animator.component.property.control.RotateComponent;
import animator.component.property.control.SliderComponent;
import animator.component.property.control.TitledBoxComponent;
import animator.component.property.interpolator.InterpolatorEditorComponent;
import animator.component.util.FocusHelper;
import animator.model.interpolator.InterpolatorModel;

import javax.inject.Inject;
import java.io.IOException;

import static animator.component.util.binding.BindingFunctions.bind;

public class PropertyEditorComponent {

    private static final String FXML_NAME = "PropertyEditorComponent.fxml";

    private final PropertyEditorPresenter presenter;
    private final ColorEditorComponent colorEditor;
    private final InterpolatorEditorComponent interpolatorEditor;

    @FXML
    private ScrollPane root;
    @FXML
    private VBox scrollPaneContent;
    @FXML
    private TitledBoxComponent layoutBox;
    @FXML
    private TitledBoxComponent colorBox;
    @FXML
    private TitledBoxComponent shapeBox;
    @FXML
    private TitledBoxComponent transformBox;
    @FXML
    private TitledBoxComponent interpolatorBox;
    @FXML
    private DoubleInputComponent layoutXInput;
    @FXML
    private DoubleInputComponent layoutYInput;
    @FXML
    private DoubleInputComponent widthInput;
    @FXML
    private DoubleInputComponent heightInput;
    @FXML
    private CheckBox snapToGridCheckBox;
    @FXML
    private DoubleInputComponent strokeWidthInput;
    @FXML
    private DoubleInputComponent arcWidthInput;
    @FXML
    private DoubleInputComponent arcHeightInput;
    @FXML
    private SliderComponent opacityInput;
    @FXML
    private DoubleInputComponent translateXInput;
    @FXML
    private DoubleInputComponent translateYInput;
    @FXML
    private DoubleInputComponent translateZInput;
    @FXML
    private RotateComponent rotateInput;
    @FXML
    private DoubleInputComponent scaleXInput;
    @FXML
    private DoubleInputComponent scaleYInput;
    @FXML
    private DoubleInputComponent scaleZInput;

    @Inject
    public PropertyEditorComponent(PropertyEditorPresenter presenter, ColorEditorComponent colorEditor, InterpolatorEditorComponent interpolatorEditor) {
        this.presenter = presenter;
        this.colorEditor = colorEditor;
        this.interpolatorEditor = interpolatorEditor;
        loadFxml();
        initUi();
        addSubEditors();
        configureInterpolators();
        initPresenter();
    }

    public Region getRoot() {
        return root;
    }

    TitledBoxComponent getLayoutBox() {
        return layoutBox;
    }

    TitledBoxComponent getColorBox() {
        return colorBox;
    }

    TitledBoxComponent getShapeBox() {
        return shapeBox;
    }

    TitledBoxComponent getTransformBox() {
        return transformBox;
    }

    DoubleInputComponent getLayoutXInput() {
        return layoutXInput;
    }

    DoubleInputComponent getLayoutYInput() {
        return layoutYInput;
    }

    DoubleInputComponent getWidthInput() {
        return widthInput;
    }

    DoubleInputComponent getHeightInput() {
        return heightInput;
    }

    CheckBox getSnapToGridCheckBox() {
        return snapToGridCheckBox;
    }

    DoubleInputComponent getStrokeWidthInput() {
        return strokeWidthInput;
    }

    DoubleInputComponent getArcWidthInput() {
        return arcWidthInput;
    }

    DoubleInputComponent getArcHeightInput() {
        return arcHeightInput;
    }

    SliderComponent getOpacityInput() {
        return opacityInput;
    }

    DoubleInputComponent getTranslateXInput() {
        return translateXInput;
    }

    DoubleInputComponent getTranslateYInput() {
        return translateYInput;
    }

    DoubleInputComponent getTranslateZInput() {
        return translateZInput;
    }

    RotateComponent getRotateInput() {
        return rotateInput;
    }

    DoubleInputComponent getScaleXInput() {
        return scaleXInput;
    }

    DoubleInputComponent getScaleYInput() {
        return scaleYInput;
    }

    DoubleInputComponent getScaleZInput() {
        return scaleZInput;
    }

    ColorEditorComponent getColorEditor() {
        return colorEditor;
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initUi() {
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        FocusHelper.requestFocusOnPress(root);
        FocusHelper.suppressFocusStyleOnPress(snapToGridCheckBox);
    }

    private void addSubEditors() {
        colorBox.setContent(colorEditor.getRoot());
        interpolatorBox.setContent(interpolatorEditor.getRoot());
    }

    private void configureInterpolators() {
        bindInterpolators(layoutXInput, layoutYInput, widthInput, heightInput, strokeWidthInput, arcWidthInput, arcHeightInput, opacityInput, translateXInput,
                          translateYInput, translateZInput, rotateInput, scaleXInput, scaleYInput, scaleZInput);

        bind(colorEditor.getInterpolatorButton().itemsProperty(), interpolatorEditor.getInterpolators(), InterpolatorModel.class::cast);
    }

    private void bindInterpolators(DoubleInputComponent... doubleInputComponents) {
        for (DoubleInputComponent doubleInputComponent : doubleInputComponents) {
            bind(doubleInputComponent.getInterpolators(), interpolatorEditor.getInterpolators(), InterpolatorModel.class::cast);
        }
    }

    private void initPresenter() {
        presenter.setView(this);
    }
}
