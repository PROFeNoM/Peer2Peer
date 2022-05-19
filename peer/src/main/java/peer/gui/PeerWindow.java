package peer.gui;

import peer.Peer;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import java.awt.GridLayout;

public class PeerWindow extends JFrame {

    public PeerWindow() {
        super("Peer interface");
        setSize(700, 700);

        JLayeredPane layeredPane = new JLayeredPane();
        this.add(layeredPane);
        layeredPane.setSize(this.getSize());
        layeredPane.setLayout(new GridLayout(1, 1));

        Peer p = new Peer();

        ConnectedPanel connectedPanel = new ConnectedPanel(p);
        PeerPanel peerPanel = new PeerPanel(p, connectedPanel);

        layeredPane.add(peerPanel, 1);
        layeredPane.add(connectedPanel, 2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
