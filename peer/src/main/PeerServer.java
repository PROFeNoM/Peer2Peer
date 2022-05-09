package peer.src.main;

import java.io.*;
import java.net.*;

// Server to listen for connections from peers
public class PeerServer extends Thread {
    private ServerSocket serverSocket;
    private Peer peer;
    public PeerServer(int port, Peer peer) throws IOException {
        serverSocket = new ServerSocket(port);
        this.peer = peer;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Logger.log(getClass().getSimpleName(), "Peer connected");
                new ClientHandler(clientSocket, peer).start();
            }
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void close() {
        try {
            serverSocket.close();
            this.stop();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Cannot close server: " + e.getMessage());
        }
    }
}
