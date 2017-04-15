package animator.util;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class MathFunctions {

    public static boolean canParseInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean canParseDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Bounds union(Bounds first, Bounds second) {
        if (first == null) {
            return second;
        } else if (second == null) {
            return first;
        }
        double minX = Math.min(first.getMinX(), second.getMinX());
        double minY = Math.min(first.getMinY(), second.getMinY());
        double maxX = Math.max(first.getMaxX(), second.getMaxX());
        double maxY = Math.max(first.getMaxY(), second.getMaxY());
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }
}
