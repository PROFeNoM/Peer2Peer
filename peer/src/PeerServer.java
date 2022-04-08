import java.io.*;
import java.net.*;

// Class for starting the peer server and listen for connections
public class PeerServer extends Thread {
    private ServerSocket socket;

    public PeerServer(ServerSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = socket.accept();
                System.out.println("Connected to a peer");
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}