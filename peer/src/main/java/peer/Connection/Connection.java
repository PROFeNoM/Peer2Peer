package peer.connection;

import peer.util.Logger;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;

/**
 * This class represents a connection through a socket.
 * It can be used to send and receive messages through the socket.
 */
class Connection {
    /**
     * Socket used to send and receive messages.
     */
    private final Socket socket;

    /**
     * Buffered reader used to read messages from the socket.
     */
    private final BufferedReader in;

    /**
     * Print writer used to send messages to the socket.
     */
    private final PrintWriter out;

    /**
     * Create a new connection through the given socket.
     * 
     * @param socket Socket to use for the connection.
     * @throws IOException If an error occurs while creating the output stream.
     */
    protected Connection(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Create a new connection with the given ip and port.
     * 
     * @param ip IP address to connect to.
     * @param port Port to connect to.
     * @throws IOException If an error occurs while creating the socket or the output stream.
     */
    protected Connection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Returns the connection state of the socket.
     * 
     * @return True if the socket is connected, false otherwise.
     */
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Returns the closed state of the socket.
     * 
     * @return True if the connection is closed, false otherwise.
     */
    public boolean isClosed() {
        return socket.isClosed();
    }

    /**
     * Send a message to the connection.
     * TODO: make it protected and handle by child ?
     * @param message Message to send.
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Get a message from the connection.
     * TODO: make it protected and handle by child ?
     * @return The message received or an empty string if an error occurred.
     */
    public String getMessage() {
        String message = "";
        try {
            message = in.readLine();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
        return message;
    }

    /** 
     * Close the connection.
     * Close the socket and the input and output streams.
     * @throws IOException If an error occurs while closing the socket or the input stream.
     */
    public void stop() throws IOException {
            out.println("exit");
            in.close();
            out.close();
            socket.close();
    }
}
