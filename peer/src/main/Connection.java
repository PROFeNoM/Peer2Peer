package peer.src.main;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;

public class Connection {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Connection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Send a message
    public void sendMessage(String message) {
        out.println(message);
    }

    // Get a message
    public String getMessage() {
        String message = "";
        try {
            message = in.readLine();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
        return message;
    }

    // Close the connection
    public void stop() throws IOException {
            out.println("exit");
            in.close();
            out.close();
            socket.close();
    }
}
