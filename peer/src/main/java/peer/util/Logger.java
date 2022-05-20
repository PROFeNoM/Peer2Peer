package peer.util;

/**
 * Class to print messages to the console.
 * It can be used to print debug messages (only if debug mode is enabled), error messages and warning messages.
 */
public class Logger {
    private static void log(String message) {
        if (Configuration.getInstance().isDebug()) {
            System.out.println(message);
        }
    }

    public static void debug(String message) {
        log("[DEBUG] " + message);
    }

    public static void debug(String name, String message) {
        log("[DEBUG][" + name + "] " + message);
    }

    public static void warn(String message) {
        System.out.println("[WARN] " + message);
    }

    public static void warn(String name, String message) {
        System.out.println("[WARN][" + name + "] " + message);
    }

    public static void error(String message) {
        System.err.println("[ERROR] " + message);
    }

    public static void error(String name, String message) {
        System.err.println("[ERROR][" + name + "] " + message);
    }
}
