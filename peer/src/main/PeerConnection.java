package peer.src.main;

import java.net.*;
import java.io.*;

// Class to talk to another peer
public class PeerConnection {
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    public PeerConnection(Socket socket) {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void stop() {
        try {
        in.close();
        out.close();
        socket.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getMessage() {
        String message = "";
        try {
            message = in.readLine();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
        return message;
    }
}
