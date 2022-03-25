import java.io.IOException;
import java.util.concurrent.TimeUnit ;
class TestTracker {
    public static void main(String[] args) {
        Tracker tracker = new Tracker();
        tracker.start(Integer.parseInt(args[0]));
        int nb_messages = 5;
        try {
            for (int i = 0; i < nb_messages; i++) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

    }
}