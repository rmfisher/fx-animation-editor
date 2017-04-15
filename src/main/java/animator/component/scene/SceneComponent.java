package animator.component.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import animator.component.scene.panning.PanningComponent;
import animator.component.scene.selection.ResizerComponent;
import animator.component.scene.selection.SelectionBoxComponent;
import animator.component.util.FocusHelper;
import animator.component.util.icon.Svg;

import javax.inject.Inject;

public class SceneComponent {

    private static final String STYLE_CLASS = "scene-component";
    private static final String STYLESHEET = "scene-component.css";

    private final StackPane root = new StackPane();
    private final Group sceneContent = new Group();
    private final Button addButton = new Button();
    private final ScenePresenter presenter;
    private final ResizerComponent resizerComponent;
    private final PanningComponent panningComponent;
    private final SelectionBoxComponent selectionBox;

    @Inject
    public SceneComponent(ScenePresenter presenter, PanningComponent panningComponent, ResizerComponent resizerComponent,
                          SelectionBoxComponent selectionBox) {

        this.presenter = presenter;
        this.resizerComponent = resizerComponent;
        this.panningComponent = panningComponent;
        this.selectionBox = selectionBox;
        initUi();
        initPresenter();
    }

    public Node getRoot() {
        return root;
    }

    Group getSceneContent() {
        return sceneContent;
    }

    Button getAddButton() {
        return addButton;
    }

    private void initUi() {
        root.setMinSize(0, 0);
        root.setPrefSize(1, 1); // Decouple from children.
        root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.getStyleClass().add(STYLE_CLASS);
        root.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
        root.setAlignment(Pos.TOP_LEFT);
        root.getChildren().addAll(panningComponent.getRoot(), addButton);

        panningComponent.getChildren().addAll(sceneContent, resizerComponent.getRoot(), selectionBox.getRoot());

        StackPane.setMargin(addButton, new Insets(15));
        addButton.setGraphic(Svg.PLUS_FAT.node());
        addButton.setFocusTraversable(false);

        FocusHelper.requestFocusOnPress(panningComponent.getRoot());
    }

    private void initPresenter() {
        presenter.setView(this);
    }
}
