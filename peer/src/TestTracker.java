import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class TestTracker {
    public static void main(String[] args) {
        int port;
    
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Usage: java TestTracker <port>");
                return;
            }
        } else {
            port = Configuration.getInstance().getTrackerPort();
        }

        Tracker tracker = new Tracker(port);
        System.out.println("Tracker started");
        tracker.run();
        tracker.stop();
        System.out.println("Tracker stopped");
    }
}