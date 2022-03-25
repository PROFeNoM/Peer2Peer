public class TestPeer {
    public static void main(String[] args) {
        Peer peer = new Peer();
        peer.startConnection(args[0], Integer.parseInt(args[1]));
        String response = peer.sendMessage("hello server");
        System.out.println("Response 1 : "+response);

        response = peer.sendMessage("hello world");
        System.out.println("Response 2 : "+response);
        peer.stopConnection();
    }
}