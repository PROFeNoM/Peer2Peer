package peer.src.main;

import peer.src.main.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComponent;

import java.awt.GridLayout;

public class PeerWindow extends JFrame {
    
    public PeerWindow() {
        super("Test");
        setSize(700, 700);

        Peer p = new Peer();
        this.setLayout(new GridLayout(3, 1));
        this.add(new JLabel("Peer Interface"));
        JLabel jl1 = new JLabel();
        JLabel jl2 = new JLabel();
        GridLayout middle = new GridLayout(1, 2);
        jl1.setLayout(middle);
        JTextField file = new JTextField("Add File");
        PeerButton addFile = new PeerButton(p, "Add file", file);
        jl1.add(file);
        jl1.add(addFile);
        this.add(jl1);

        GridLayout bottom = new GridLayout(1, 2);
        PeerButton start = new PeerButton(p, "Start");
        PeerButton stop = new PeerButton(p, "Stop");
        jl2.setLayout(bottom);
        jl2.add(start);
        jl2.add(stop);
        this.add(jl2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
