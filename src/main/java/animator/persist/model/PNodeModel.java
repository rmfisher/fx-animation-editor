package animator.persist.model;

import java.util.List;

public class PNodeModel {

    private int id;
    private PNodeType type;
    private List<PNodeModel> children;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PNodeType getType() {
        return type;
    }

    public void setType(PNodeType type) {
        this.type = type;
    }

    public List<PNodeModel> getChildren() {
        return children;
    }

    public void setChildren(List<PNodeModel> children) {
        this.children = children;
    }
}
