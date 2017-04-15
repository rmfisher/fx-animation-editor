package animator.component.menu;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import animator.command.CommandStack;
import animator.component.scene.panning.PanningHelper;
import animator.event.MenuActionEvent;
import animator.util.OsHelper;

import java.io.IOException;
import java.util.function.Consumer;

public class MenuBarComponent {

    private static final String FXML_NAME = "MenuBarComponent.fxml";
    private static final String WINDOWS_10_PSEUDO_CLASS = "win10";

    private final EventBus eventBus;
    private final CommandStack commandStack;
    private final PanningHelper panningHelper;

    @FXML
    private MenuBar root;
    @FXML
    private Menu fileMenu;
    @FXML
    private MenuItem newMenuItem;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private MenuItem saveAsMenuItem;
    @FXML
    private MenuItem openLogoMenuItem;
    @FXML
    private MenuItem openSlackMenuItem;
    @FXML
    private MenuItem exitMenuItem;
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;
    @FXML
    private MenuItem cutMenuItem;
    @FXML
    private MenuItem copyMenuItem;
    @FXML
    private MenuItem pasteMenuItem;
    @FXML
    private MenuItem selectAllMenuItem;
    @FXML
    private MenuItem deleteMenuItem;
    @FXML
    private MenuItem clearSelectionMenuItem;
    @FXML
    private MenuItem groupMenuItem;
    @FXML
    private MenuItem ungroupMenuItem;
    @FXML
    private MenuItem toFrontMenuItem;
    @FXML
    private MenuItem toBackMenuItem;
    @FXML
    private MenuItem panToOriginMenuItem;
    @FXML
    private MenuItem panToContentMenuItem;

    @Inject
    public MenuBarComponent(EventBus eventBus, CommandStack commandStack, PanningHelper panningHelper) {
        this.eventBus = eventBus;
        this.commandStack = commandStack;
        this.panningHelper = panningHelper;
        loadFxml();
        initUi();
        initActions();
        bindDisabledStates();
    }

    public Node getRoot() {
        return root;
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
        root.pseudoClassStateChanged(PseudoClass.getPseudoClass(WINDOWS_10_PSEUDO_CLASS), OsHelper.isWindows10());
        if (OsHelper.isMac()) {
            fileMenu.getItems().remove(exitMenuItem);
        }
    }

    private void initActions() {
        newMenuItem.setOnAction(event -> doAction(MenuActionEvent.NEW));
        openMenuItem.setOnAction(event -> doAction(MenuActionEvent.OPEN));
        saveMenuItem.setOnAction(event -> doAction(MenuActionEvent.SAVE));
        saveAsMenuItem.setOnAction(event -> doAction(MenuActionEvent.SAVE_AS));
        openLogoMenuItem.setOnAction(event -> doAction(MenuActionEvent.OPEN_LOGO));
        openSlackMenuItem.setOnAction(event -> doAction(MenuActionEvent.OPEN_SLACK));
        exitMenuItem.setOnAction(event -> doAction(MenuActionEvent.EXIT));
        undoMenuItem.setOnAction(event -> doAction(MenuActionEvent.UNDO));
        redoMenuItem.setOnAction(event -> doAction(MenuActionEvent.REDO));
        cutMenuItem.setOnAction(event -> callOnTextFieldIfFocused(TextInputControl::cut, MenuActionEvent.CUT));
        copyMenuItem.setOnAction(event -> callOnTextFieldIfFocused(TextInputControl::copy, MenuActionEvent.COPY));
        pasteMenuItem.setOnAction(event -> callOnTextFieldIfFocused(TextInputControl::paste, MenuActionEvent.PASTE));
        selectAllMenuItem.setOnAction(event -> callOnTextFieldIfFocused(TextInputControl::selectAll, MenuActionEvent.SELECT_ALL));
        deleteMenuItem.setOnAction(event -> doAction(MenuActionEvent.DELETE_SELECTION));
        clearSelectionMenuItem.setOnAction(event -> doAction(MenuActionEvent.CLEAR_SELECTION));
        groupMenuItem.setOnAction(event -> doAction(MenuActionEvent.GROUP));
        ungroupMenuItem.setOnAction(event -> doAction(MenuActionEvent.UNGROUP));
        toFrontMenuItem.setOnAction(event -> doAction(MenuActionEvent.TO_FRONT));
        toBackMenuItem.setOnAction(event -> doAction(MenuActionEvent.TO_BACK));
        panToOriginMenuItem.setOnAction(event -> panningHelper.panToOrigin());
        panToContentMenuItem.setOnAction(event -> panningHelper.panToContent());
    }

    private void doAction(MenuActionEvent action) {
        eventBus.post(action);
    }

    private void callOnTextFieldIfFocused(Consumer<TextInputControl> consumer, MenuActionEvent actionToPostOtherwise) {
        if (root.getScene().getFocusOwner() instanceof TextField) {
            consumer.accept(((TextInputControl) root.getScene().getFocusOwner()));
        } else {
            doAction(actionToPostOtherwise);
        }
    }

    private void bindDisabledStates() {
        undoMenuItem.disableProperty().bind(commandStack.canUndoProperty().not());
        redoMenuItem.disableProperty().bind(commandStack.canRedoProperty().not());
    }
}
