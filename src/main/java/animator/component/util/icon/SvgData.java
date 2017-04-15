package animator.component.util.icon;

public class SvgData {

    private final String name;
    private double width;
    private double height;
    private final SvgLayerData[] layers;

    public SvgData(String name, double width, double height, SvgLayerData... layers) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.layers = layers;
    }

    public String getName() {
        return name;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public SvgLayerData[] getLayers() {
        return layers;
    }
}
