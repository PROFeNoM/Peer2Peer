package peer.connection;

import java.io.IOException;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestConnection {
    @Test
    public void testInitialisationIpPort() throws IOException {
        final Connection connection = new Connection("localhost", 1234);

        assertTrue(connection.isConnected());
        assertFalse(connection.isClosed());

        connection.stop();

        assertTrue(connection.isClosed());
    }

    @Test
    public void testInitialisationSocket() throws IOException {
        final Socket socket = new Socket("localhost", 1234);

        Connection connection = new Connection(socket);

        assertTrue(socket.isConnected());
        assertFalse(socket.isClosed());

        connection.stop();

        assertTrue(socket.isClosed());
    }
}