package animator.persist;

public enum PropertyKey {

    DIVIDER_POSITION("divider.position"),
    EXPANDED_BOXES("expanded.boxes"),
    LAST_DIRECTORY("last.directory"),
    LAST_EDITED_FILE("last.edited.file"),
    STAGE_POSITION("stage.position"),
    STAGE_MAXIMIZED("stage.maximized");

    private final String key;

    PropertyKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
