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

public class PeerWindow extends JFrame {
    
    public PeerWindow() {
        super("Test");
        setSize(700, 700);

        Peer p = new Peer();
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
        PeerButton addFileButton = new PeerButton(p, "Add file(s)");
        PeerButton removeFileButton = new PeerButton(p, "Remove file(s)");
        panel1_1.add(addFileButton);
        panel1_1.add(removeFileButton);
        jl1.add(panel1_1, BorderLayout.CENTER);
        this.add(jl1);

        GridLayout startStop = new GridLayout(1, 3);
        PeerButton check = new PeerButton(p, "Check");
        PeerButton start = new PeerButton(p, "Start", inter);
        PeerButton stop = new PeerButton(p, "Stop", inter);
        start.setBackground(java.awt.Color.GREEN);
        start.setOpaque(true);
        stop.setBackground(java.awt.Color.RED);
        stop.setOpaque(true);

        jl3.setLayout(startStop);
        jl3.add(check);
        jl3.add(start);
        jl3.add(stop);
        this.add(jl3);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
