import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit ;
class TestTracker {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("usage: java TestTracker <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        Tracker tracker = new Tracker(port);
        System.out.println("Tracker started");
        tracker.run();
        tracker.stop();
        System.out.println("Tracker stopped");
    }
}