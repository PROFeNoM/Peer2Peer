import java.io.*;
import java.net.*;

public class PeerServer extends Thread {
    private ServerSocket socket;

    public PeerServer(ServerSocket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            while (true) {
                Socket clientSocket = socket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
