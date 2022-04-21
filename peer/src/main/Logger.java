package peer.src.main;

public class Logger {
    public static void log(String message) {
        System.out.println("[LOG] " + message);
    }

    public static void log(String name, String message) {
        System.out.println("[LOG][" + name + "] " + message);
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
