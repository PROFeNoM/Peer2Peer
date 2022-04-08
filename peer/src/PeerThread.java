import java.io.*;
import java.net.*;

// Class for communicate with another peer
public class PeerThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public PeerThread(Socket socket) {
        this.socket = socket;
        try {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Cannot connect to peer: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.isEmpty())
                    System.out.println("> " + inputLine);

                if (inputLine.equals(".")) {
                    out.println("good bye");
                    break;
                }
                out.println("200");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
