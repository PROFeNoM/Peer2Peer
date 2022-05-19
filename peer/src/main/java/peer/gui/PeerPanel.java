package peer.gui;

import peer.Peer;

import java.io.File;
import java.util.GregorianCalendar;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class PeerPanel extends JPanel {
    Peer p;

    public PeerPanel(Peer p, ConnectedPanel connectedPanel) {
        super();
        this.p = p;
        // First layout : Connection panel
        this.setLayout(new GridLayout(3, 1));
        JLabel inter = new JLabel("Peer Interface : Not Connected yet");
        this.add(inter);
        JPanel jl1 = new JPanel();
        JPanel jl2 = new JPanel();
        JPanel jl3 = new JPanel();
        JPanel panel1_1 = new JPanel();
        JPanel panel2_1 = new JPanel();
        GridLayout add = new GridLayout(1, 2);
        // jl1.setLayout(add);

        BorderLayout border = new BorderLayout();
        jl1.setLayout(border);
        jl1.add(new JLabel("Add a file from the seeds folder"), BorderLayout.NORTH);
        panel1_1.setLayout(add);

        // TODO : add a copy file to the seed folder
        PeerButton addFileButton = new PeerButton(p, "Add file(s)", connectedPanel);
        PeerButton removeFileButton = new PeerButton(p, "Remove file(s)");
        panel1_1.add(addFileButton);
        panel1_1.add(removeFileButton);
        jl1.add(panel1_1, BorderLayout.CENTER);
        this.add(jl1);

        GridLayout startStop = new GridLayout(1, 2);
        PeerButton start = new PeerButton(p, "Start", inter);
        PeerButton stop = new PeerButton(p, "Stop", inter);
        start.setBackground(java.awt.Color.GREEN);
        start.setOpaque(true);
        stop.setBackground(java.awt.Color.RED);
        stop.setOpaque(true);

        jl3.setLayout(startStop);
        jl3.add(start);
        jl3.add(stop);
        this.add(jl3);
    }

    public Peer getPeer() {
        return this.p;
    }

}
