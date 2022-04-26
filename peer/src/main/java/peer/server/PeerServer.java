package peer.server;

import java.io.*;
import java.net.*;

import peer.util.Logger;

/**
 * Server to listen for connections from peers.
 */
public class PeerServer extends Thread {
    /**
     * Socket to listen for connections.
     */
    private final ServerSocket serverSocket;

    /**
     * Start a PeerServer on the given port.
     * 
     * @param port Port to listen on.
     * @throws IOException If the server socket cannot be created.
     */
    public PeerServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    /**
     * Run the server.
     * 
     * Accept connections and start a new thread for each connection.
     */
    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                Logger.log(getClass().getSimpleName(), "Peer connected");
                new ClientHandler(clientSocket).start();
            } catch (IOException e) {
                Logger.error(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    /**
     * Close the server socket.
     */
    public void close() throws IOException {
        serverSocket.close();
    }
}
