package peer.gui;

import peer.Peer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.GridLayout;

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
        JPanel panel1 = new JPanel();
        GridLayout add = new GridLayout(1, 2);

        BorderLayout border = new BorderLayout();
        jl1.setLayout(border);
        jl1.add(new JLabel("Add a file from the seeds folder"), BorderLayout.NORTH);
        panel1.setLayout(add);

        PeerButton addFileButton = new PeerButton(p, "Add file(s)", connectedPanel);
        PeerButton removeFileButton = new PeerButton(p, "Remove file(s)");
        panel1.add(addFileButton);
        panel1.add(removeFileButton);
        jl1.add(panel1, BorderLayout.CENTER);
        this.add(jl1);

        GridLayout startStop = new GridLayout(1, 2);
        PeerButton start = new PeerButton(p, "Start", inter);
        PeerButton stop = new PeerButton(p, "Stop", inter);
        start.setBackground(java.awt.Color.GREEN);
        start.setOpaque(true);
        stop.setBackground(java.awt.Color.RED);
        stop.setOpaque(true);

        jl2.setLayout(startStop);
        jl2.add(start);
        jl2.add(stop);
        this.add(jl2);
    }

    public Peer getPeer() {
        return this.p;
    }

}
