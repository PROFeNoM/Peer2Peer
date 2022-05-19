package peer;

import peer.util.Configuration;

/**
 * Main class of the application.
 * It launches the user peer with the command-line arguments
 * or the parameters in the configuration file if no parameters are provided.
 */
public class Main {
    public static void main(String[] args) {
        final String trackerIp;
        final int trackerPort;
        final int peerPort;

        for (String arg : args) {
            if (arg.equals("-h")) {
                System.out.println("Usage: java [-D tracker-ip=<trackerIp>] [-D tracker-port=<trackerPort>] [-D peer-port=<peerPort>] peer.src.main.Main [-h]");
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
