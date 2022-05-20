package peer;

import peer.util.Configuration;

public class Main {
    public static void main(String[] args) {
        String asciiArt = " ______ _      _    ___   _____ _                     \n" +
                          "|  ____(_)    | |  |__ \\ / ____| |                   \n" +
                          "| |__   _ _ __| |__   ) | (___ | |__   __ _ _ __ ___ \n" +
                          "|  __| | | '__| '_ \\ / / \\___ \\| '_ \\ / _` | '__/ _ \\\n" +
                          "| |____| | |  | |_) / /_ ____) | | | | (_| | | |  __/\n" +
                          "|______|_|_|  |_.__/____|_____/|_| |_|\\__,_|_|  \\___|\n";

        System.out.println(asciiArt);

        int peerPort, maxPeers;
        for (String arg : args) {
            System.out.println(arg);
            if (arg.equals("-h")) {
                System.out.println("Usage: java [-D peerPort=<peerPort>] [-D peerMax=<peerMax>] peer.src.main.Main [-h]");
                System.exit(0);
            }
        }

        peerPort = System.getProperty("peerPort") == null
                ? Configuration.getInstance().getPeerPort()
                : Integer.parseInt(System.getProperty("peerPort"));

        maxPeers = System.getProperty("peerMax") == null
                ? Configuration.getInstance().getMaxPeerToConnect()
                : Integer.parseInt(System.getProperty("peerMax"));

        Peer peer = new Peer();
        peer.start(peerPort, "0.0.1", maxPeers);
        peer.run();
        peer.stop();
    }
}
