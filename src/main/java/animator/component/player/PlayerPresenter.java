package animator.component.player;

import animator.model.PlayerModel;

import javax.inject.Inject;

public class PlayerPresenter {

    private static final String CURRENT_TIME_FORMAT = "%.2f";
    private static final String FINAL_TIME_FORMAT = " / %.2fs";

    private final PlayerModel model;
    private PlayerComponent view;

    @Inject
    public PlayerPresenter(PlayerModel model) {
        this.model = model;
    }

    void setView(PlayerComponent view) {
        this.view = view;
        initBindings();
        initActions();
    }

    private void initBindings() {
        view.getCurrentTimeLabel().textProperty().bind(model.currentTimeProperty().asString(CURRENT_TIME_FORMAT));
        view.getFinalTimeLabel().textProperty().bind(model.finalTimeProperty().asString(FINAL_TIME_FORMAT));
    }

    private void initActions() {
        view.getPlayButton().setOnAction(event -> onPlayButtonPressed());
        view.getPauseButton().setOnAction(event -> onPauseButtonPressed());
        view.getStopButton().setOnAction(event -> onStopButtonPressed());
    }

    private void onPlayButtonPressed() {
        if (model.isAnimationPossible()) {
            model.setPlayMode(true);
            model.setRunning(true);
        }
    }

    private void onPauseButtonPressed() {
        if (model.isAnimationPossible()) {
            model.setRunning(!model.isRunning());
        }
    }

    private void onStopButtonPressed() {
        if (model.isAnimationPossible()) {
            model.setPlayMode(false);
            model.setRunning(false);
        }
    }
}
