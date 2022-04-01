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

        Tracker tracker = new Tracker();
        int port = Integer.parseInt(args[0]);
        tracker.start(port);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String inputLine;
        try {
            while (!(inputLine = reader.readLine()).equals("stop")){
                System.out.println(inputLine);
            }
            System.out.println(inputLine);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        tracker.stop();
        System.out.println("Tracker stopped");
    }
}