package peer.src.main;

import java.io.*;
import java.net.*;

// Class to talk to the tracker
public class Tracker {
    private String ip;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    Tracker(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    // Connect to the tracker on the given ip and port
    public void connect() throws IOException, UnknownHostException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Send the initial announce message to the tracker
    public void announce(int peerPort, String seeds, String leeches) {
        String message = "announce";
        message += " listen " + peerPort;
        message += " seed " + seeds;
        message += " leech " + leeches;
        out.println(message);

        try {
            String response = in.readLine();
            if (response.equals("ok")) {
                Logger.log(getClass().getSimpleName(), "Announced to tracker");
            } else {
                Logger.error(getClass().getSimpleName(), "Failed to announce to tracker: " + response);
                System.exit(1);
            }
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Failed to announce to tracker: " + e.getMessage());
            System.exit(1);
        }
    }

    // Send a message to the tracker and return the response
    public String sendMessage(String msg) {
        out.println(msg);
        String response = "";
        try {
            response = in.readLine();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), e.getMessage());
            System.exit(1);
        }
        return response;
    }

    // Close the connection to the tracker and stop the server
    public void stop() {
        try {
            out.println("exit");
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            Logger.error(getClass().getSimpleName(), "Error while stopping tracker: " + e.getMessage());
            System.exit(1);
        }
    }
}
