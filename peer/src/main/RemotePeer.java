package peer.src.main;

import java.net.*;
import java.io.*;

// Class to talk to another peer
public class RemotePeer {
    String ip;
    int port;
    Socket socket;
    BufferedReader in;
    PrintWriter out;


    public RemotePeer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    // Connect to the peer on the given ip and port
    public void connect() {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Logger.log(getClass().getSimpleName(), "Connected to peer");
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
    }

    public void close() {
        try {
        in.close();
        out.close();
        socket.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String response = "";
        try {
            response = in.readLine();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
        }
        return response;
    }
}
