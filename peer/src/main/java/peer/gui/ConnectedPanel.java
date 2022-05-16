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

public class ConnectedPanel extends JPanel {
    private PeerPanel peerPanel;
    private Peer peer;

    public ConnectedPanel(PeerPanel pp) {
        super();
        this.peerPanel = pp;
        this.peer = pp.getPeer();

        // Second layout : Connected panel
        this.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JLabel connected = new JLabel("Peer Interface : Connected");
        connected.setBackground(java.awt.Color.GREEN);
        connected.setOpaque(true);
        topPanel.add(connected);

        // The panel where the looked files will be listed
        GetFilesPanel filesPanel = new GetFilesPanel();
        JPanel files = new JPanel();
        filesPanel.setLayout(new FlowLayout());
        filesPanel.add(files);


        GetFilesPanel getFilePanel = new GetFilesPanel();
        JPanel lookPanel = new JPanel();
        GetFilesButton look = new GetFilesButton(peerPanel.getPeer(), "Look", getFilePanel);
        lookPanel.add(look);

        getFilePanel.setLayout(new FlowLayout());

        
        this.add(topPanel, BorderLayout.NORTH);
        this.add(filesPanel);
        this.add(lookPanel, BorderLayout.SOUTH);
    }
}
