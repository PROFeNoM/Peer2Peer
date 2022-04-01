import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.lang.Thread;

public class Peer {
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }

    public String sendMessage(String msg) {
        out.println(msg);
        String response;
        try {
            response = in.readLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            response = "rien";
            System.exit(-1);
        }
        return response;
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public String getHash(File file) {
        MessageDigest md;
        StringBuilder myHash;

        try{
            //Get instance of MD5
            md = MessageDigest.getInstance("MD5");

            //Get file input stream for reading the file content
            FileInputStream fis = new FileInputStream(file);
            
            //Create byte array to read data in chunks
            byte[] byteArray = new byte[1024];
            int bytesCount = 0; 
                
            //Read file data and update in md
            while ((bytesCount = fis.read(byteArray)) != -1) {
                md.update(byteArray, 0, bytesCount);
            };
            
            fis.close();
            
            //Get the hash's bytes
            byte[] bytes = md.digest();
            
            //Convert bytes to hexadecimal format
            myHash = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                myHash.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        }catch(Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return myHash.toString();
    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine != null){
                    System.out.println(inputLine);
                    out.println(inputLine);
                }
                if (".".equals(inputLine)) {
                    out.println("good bye");
                    break;
                }
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    // // a function that starts a server on given socket
    // int startServer(int port) {
    //     try {
    //         ServerSocket serverSocket = new ServerSocket(port);
    //         System.out.println("Server started on port " + port);
    //         while (true) {
    //             Socket clientSocket = serverSocket.accept();
    //             System.out.println("Client connected");
    //             // new Thread(new ServerThread(clientSocket)).start();
    //         }
    //     } catch (IOException e) {
    //         System.out.println(e.getMessage());
    //         System.exit(-1);
    //     }
    //     return 0;
    // }
}
