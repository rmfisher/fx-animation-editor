package animator.component.property.interpolator;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import animator.component.property.control.SliderComponent;
import animator.component.property.interpolator.plot.InterpolatorPlotComponent;
import animator.component.util.FocusHelper;
import animator.component.util.binding.ChainedBindingFunctions;
import animator.model.interpolator.InterpolatorModel;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class InterpolatorEditorComponent {

    private static final int NUMBER_OF_SLIDERS = 6;  // 6 is the most sliders we will need for overshoot, bounce, and spline parameters.
    private static final String FXML_NAME = "InterpolatorEditorComponent.fxml";

    private final InterpolatorPlotComponent interpolatorPlotComponent = new InterpolatorPlotComponent();
    private final List<SliderComponent> sliders = new ArrayList<>();
    private final InterpolatorEditorPresenter presenter;

    @FXML
    private VBox root;
    @FXML
    private ComboBox<InterpolatorModel> interpolatorComboBox;

    @Inject
    public InterpolatorEditorComponent(InterpolatorEditorPresenter presenter) {
        this.presenter = presenter;
        loadFxml();
        initUi();
        initPresenter();
    }

    public Region getRoot() {
        return root;
    }

    public ObservableList<InterpolatorModel> getInterpolators() {
        return presenter.getInterpolators();
    }

    ComboBox<InterpolatorModel> getInterpolatorComboBox() {
        return interpolatorComboBox;
    }

    InterpolatorPlotComponent getInterpolatorPlotComponent() {
        return interpolatorPlotComponent;
    }

    List<SliderComponent> getSliders() {
        return sliders;
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_NAME));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUi() {
        interpolatorComboBox.setCellFactory(listView -> new InterpolatorSelectionListCell());
        interpolatorComboBox.setButtonCell(new InterpolatorSelectionListCell());

        root.getChildren().add(1, interpolatorPlotComponent.getRoot());
        interpolatorPlotComponent.getRoot().setMinHeight(Region.USE_PREF_SIZE);

        IntStream.range(0, NUMBER_OF_SLIDERS).forEach(i -> addSlider());
        FocusHelper.suppressFocusStyleOnRelease(interpolatorComboBox);
    }

    private void addSlider() {
        SliderComponent slider = new SliderComponent();
        slider.setVisible(false);
        slider.setNullValue(0.0);
        slider.managedProperty().bind(slider.visibleProperty());
        slider.setInterpolatorButtonVisible(false);
        root.getChildren().add(slider);
        sliders.add(slider);
    }

    private void initPresenter() {
        presenter.setView(this);
    }

    private class InterpolatorSelectionListCell extends ListCell<InterpolatorModel> {

        @Override
        protected void updateItem(InterpolatorModel item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                textProperty().bind(item.nameProperty());
                graphicProperty().bind(ChainedBindingFunctions.from(item.iconSupplierProperty()).mapToObject(Supplier::get));
            } else {
                textProperty().unbind();
                graphicProperty().unbind();
                setText(null);
                setGraphic(null);
            }
        }
    }
}
