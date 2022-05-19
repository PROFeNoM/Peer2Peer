package peer.connection;

public class ConnectionInfo {
    String ip;
    int port;

    public ConnectionInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String toString() {
        return ip + ":" + port;
    }
}
