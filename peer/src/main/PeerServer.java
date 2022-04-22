package peer.src.main;

import java.io.*;
import java.net.*;

// Server to listen for connections from peers
public class PeerServer extends Thread {
    private ServerSocket serverSocket;

    public PeerServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Logger.log(getClass().getSimpleName(), "Peer connected");
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot close server: " + e.getMessage());
        }
    }
}
