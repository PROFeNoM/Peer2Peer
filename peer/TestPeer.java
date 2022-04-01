import java.util.concurrent.TimeUnit ;
import java.util.Scanner;
public class TestPeer {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("usage: java TestPeer <ip> <port>");
            System.exit(1);
        }

        Peer peer = new Peer();
        int port = Integer.parseInt(args[1]);
        peer.startConnection(args[0], port);

        System.out.println("Socket created");

        String response = peer.sendMessage("hello server");
        System.out.println("Response 1 : " + response);

        response = peer.sendMessage("hello world");
        System.out.println("Response 2 : " + response);

        Scanner scan = new Scanner(System.in);
        String inputLine;
        while ((inputLine = scan.nextLine()) != "stop") {
            System.out.println(inputLine);
        }
        scan.close();

        // try {
        //     TimeUnit.SECONDS.sleep(5);
        // } catch (InterruptedException e) {
        //     System.out.println(e.getMessage());
        // }
        peer.stopConnection();
        System.out.println("Peer stopped");
    }
}