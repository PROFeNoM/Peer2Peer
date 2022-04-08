public class TestPeer {
    public static void main(String[] args) {
        String trackerIp;
        int trackerPort;
        int peerPort;

        if (args.length > 1) {
            try {
                trackerIp = args[0];
                trackerPort = Integer.parseInt(args[1]);
                peerPort = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.out.println("Usage: java TestPeer <tracker-ip> <tracker-port> <peer-port>");
                return;
            }
        } else {
            trackerIp = Configuration.getInstance().getTrackerIp();
            trackerPort = Configuration.getInstance().getTrackerPort();
            peerPort = Configuration.getInstance().getPeerPort();
        }

        Peer peer = new Peer();
        peer.start(trackerIp, trackerPort, peerPort);
        peer.start(trackerIp, trackerPort, peerPort);
        peer.stop();
    }
}