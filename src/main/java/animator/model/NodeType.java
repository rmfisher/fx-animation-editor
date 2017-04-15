package animator.model;

public enum NodeType {

    RECTANGLE("Rectangle"), GROUP("Group");

    private final String name;

    NodeType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
