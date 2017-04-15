package animator.component.util.text;

public class DoubleParser {

    public static double parse(String value) {
        if (value != null) {
            String trimmed = value.trim();
            if (trimmed.equals(".") || trimmed.equals("-") || trimmed.equals("-.")) {
                return 0;
            }
            if (trimmed.endsWith("e") || trimmed.endsWith("E")) {
                return Double.parseDouble(trimmed + "0");
            } else {
                return Double.parseDouble(trimmed);
            }
        } else {
            return 0;
        }
    }

    public static boolean canParse(String value) {
        try {
            parse(value);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
