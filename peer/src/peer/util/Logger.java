package peer.util;

public class Logger {
    public static void log(String message) {
        System.out.println("\r[LOG] " + message);
    }

    public static void log(String name, String message) {
        System.out.println("\r[LOG][" + name + "] " + message);
    }


    public static void warn(String message) {
        System.out.println("\r[WARN] " + message);
    }

    public static void warn(String name, String message) {
        System.out.println("\r[WARN][" + name + "] " + message);
    }

    public static void error(String message) {
        System.err.println("\r[ERROR] " + message);
    }

    public static void error(String name, String message) {
        System.err.println("\r[ERROR][" + name + "] " + message);
    }
}
