package animator.component.player;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import animator.component.util.icon.Svg;

import javax.inject.Inject;

public class PlayerComponent {

    private static final String STYLESHEET = "player-component.css";
    private static final String STYLE_CLASS = "player";
    private static final String BUTTON_BOX_STYLE_CLASS = "button-box";
    private static final String CURRENT_TIME_LABEL_STYLE_CLASS = "current-time-label";

    private final StackPane root = new StackPane();
    private final Button playButton = new Button();
    private final Button pauseButton = new Button();
    private final Button stopButton = new Button();
    private final Label currentTimeLabel = new Label();
    private final Label finalTimeLabel = new Label();

    @Inject
    public PlayerComponent(PlayerPresenter presenter) {
        initUi();
        initPresenter(presenter);
    }

    public Node getRoot() {
        return root;
    }

    Button getPlayButton() {
        return playButton;
    }

    Button getPauseButton() {
        return pauseButton;
    }

    Button getStopButton() {
        return stopButton;
    }

    Label getCurrentTimeLabel() {
        return currentTimeLabel;
    }

    Label getFinalTimeLabel() {
        return finalTimeLabel;
    }

    private void initUi() {
        root.getStylesheets().add(getClass().getResource(STYLESHEET).toExternalForm());
        root.getStyleClass().add(STYLE_CLASS);

        playButton.setGraphic(Svg.PLAY.node());
        pauseButton.setGraphic(Svg.PAUSE.node());
        stopButton.setGraphic(Svg.STOP.node());

        HBox labelBox = new HBox(currentTimeLabel, finalTimeLabel);
        labelBox.setMaxWidth(Region.USE_PREF_SIZE);
        labelBox.setAlignment(Pos.CENTER_LEFT);
        StackPane.setAlignment(labelBox, Pos.CENTER_RIGHT);

        currentTimeLabel.getStyleClass().add(CURRENT_TIME_LABEL_STYLE_CLASS);

        HBox buttonBox = new HBox(playButton, pauseButton, stopButton);
        buttonBox.getStyleClass().add(BUTTON_BOX_STYLE_CLASS);
        buttonBox.setMaxWidth(Region.USE_PREF_SIZE);

        root.getChildren().addAll(buttonBox, labelBox);
    }

    private void initPresenter(PlayerPresenter presenter) {
        presenter.setView(this);
    }
}
