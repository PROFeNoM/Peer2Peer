public class TestPeer {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("usage: java TestPeer <ip> <port>");
            System.exit(1);
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        Peer peer = new Peer();
        peer.startConnection(ip, port);
        peer.sendMessage("Hello");
        peer.stopConnection();
    }
}