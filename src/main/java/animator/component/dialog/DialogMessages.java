package animator.component.dialog;

public interface DialogMessages {

    String CLOSE_DIRTY_FILE_OPEN = "You have unsaved changes. Save changes first?";
    String CLOSE_DIRTY_NO_FILE_OPEN = "You have unsaved changes. Do you want to discard them?";
    String EXIT_DIRTY_FILE_OPEN = "You have unsaved changes. Save changes before exiting?";
    String EXIT_DIRTY_NO_FILE_OPEN = "You have unsaved changes. Are you sure you want to exit?";
    String SAVE = "SAVE";
    String SAVE_AND_EXIT = "SAVE & EXIT";
    String DISCARD = "DISCARD";
    String EXIT = "EXIT";
    String CANCEL = "CANCEL";
}
