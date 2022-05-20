package peer;

import peer.util.Configuration;

/**
 * Main class of the application.
 * It launches the user peer with the command-line arguments
 * or the parameters in the configuration file if no parameters are provided.
 */
public class Main {
    public static void main(String[] args) {
        String asciiArt =
        " ______ _      _    ___   _____ _                     \n" +
        "|  ____(_)    | |  |__ \\ / ____| |                   \n" +
        "| |__   _ _ __| |__   ) | (___ | |__   __ _ _ __ ___ \n" +
        "|  __| | | '__| '_ \\ / / \\___ \\| '_ \\ / _` | '__/ _ \\\n" +
        "| |____| | |  | |_) / /_ ____) | | | | (_| | | |  __/\n" +
        "|______|_|_|  |_.__/____|_____/|_| |_|\\__,_|_|  \\___|\n";

        System.out.println(asciiArt);

        final String trackerIp;
        final int trackerPort;
        final int peerPort;

        for (String arg : args) {
            if (arg.equals("-h")) {
                System.out.println("Usage: java [-Dtracker-ip=<trackerIp>] [-Dtracker-port=<trackerPort>] [-Dpeer-port=<peerPort>] peer.src.main.Main [-h]");
                System.exit(0);
            }
        }

        trackerIp = System.getProperty("trackerIp") == null
                ? Configuration.getInstance().getTrackerIp()
                : System.getProperty("trackerIp");
        trackerPort = System.getProperty("trackerPort") == null
                ? Configuration.getInstance().getTrackerPort()
                : Integer.parseInt(System.getProperty("trackerPort"));
        peerPort = System.getProperty("peerPort") == null
                ? Configuration.getInstance().getPeerPort()
                : Integer.parseInt(System.getProperty("peerPort"));

        Peer peer = new Peer();
        peer.start(trackerIp, trackerPort, peerPort);
        peer.run();
        peer.stop();
    }
}
