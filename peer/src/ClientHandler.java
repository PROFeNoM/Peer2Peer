import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.isEmpty())
                    System.out.println("> " + inputLine);

                if (inputLine.equals(".")) {
                    out.println("good bye");
                    break;
                }
                out.println("200");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
