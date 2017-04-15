package animator.util;

import javafx.stage.Screen;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class OsHelper {

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.ROOT);

    public static boolean isWindows() {
        return OS_NAME.contains("windows");
    }

    public static boolean isMac() {
        return OS_NAME.contains("mac");
    }

    public static boolean isLinux() {
        return OS_NAME.contains("linux");
    }

    public static boolean isWindows10() {
        return isWindows() && OS_NAME.endsWith("10");
    }

    public static boolean isRetina() {
        return getPrimaryRenderScale() > 1;
    }

    private static float getPrimaryRenderScale() {
        try {
            Screen primary = Screen.getPrimary();
            Method renderScaleMethod = primary.getClass().getDeclaredMethod("getRenderScale");
            renderScaleMethod.setAccessible(true);
            return (Float) renderScaleMethod.invoke(primary);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return -1;
        }
    }
}
