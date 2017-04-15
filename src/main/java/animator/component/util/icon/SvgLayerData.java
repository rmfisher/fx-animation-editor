package animator.component.util.icon;

public class SvgLayerData {

    private final String path;
    private final String fill;

    public static SvgLayerData of(String path, String fill) {
        return new SvgLayerData(path, fill);
    }

    public SvgLayerData(String path, String fill) {
        this.path = path;
        this.fill = fill;
    }

    public String getPath() {
        return path;
    }

    public String getFill() {
        return fill;
    }
}
