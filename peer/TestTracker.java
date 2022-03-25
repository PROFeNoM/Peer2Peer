import java.io.IOException;
import java.util.concurrent.TimeUnit ;
class TestTracker {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("usage: java TestTracker <port>");
            System.exit(1);
        }

        Tracker tracker = new Tracker();
        int port = Integer.parseInt(args[0]);
        tracker.start(port);

        tracker.stop();
        System.out.println("Tracker stopped");
    }
}