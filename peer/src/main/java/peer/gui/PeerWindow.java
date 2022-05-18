package peer.gui;

import peer.Peer;

import java.io.File;
import java.util.GregorianCalendar;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLayeredPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;

public class PeerWindow extends JFrame {

    public PeerWindow() {
        super("Test");
        setSize(700, 700);

        JLayeredPane layeredPane = new JLayeredPane();
        this.add(layeredPane);
        layeredPane.setSize(this.getSize());
        layeredPane.setLayout(new GridLayout(1,1));

        Peer p = new Peer();

        ConnectedPanel connectedPanel = new ConnectedPanel(p);
        PeerPanel peerPanel = new PeerPanel(p, connectedPanel);

        layeredPane.add(peerPanel, 1);
        layeredPane.add(connectedPanel, 2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
