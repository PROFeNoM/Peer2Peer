package peer.src.main;

import java.io.File;
import peer.src.main.*;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PeerButton extends JButton {
    private Peer peer;
    private JTextField file;

    public PeerButton(Peer p, String text) {
        super(text);
        this.peer = p;
        this.addActionListener(new PeerButtonListener(text));
    }

    public PeerButton(Peer p, String text, JTextField fileName) {
        super(text);
        this.peer = p;
        this.file = fileName;
        this.addActionListener(new PeerButtonListener(text));
    }

    public void stopPeer() {
        peer.stop();
    }

    public void startPeer() {
        peer.start("127.0.0.1", 1234, 4321);
    }

    public void addFile() {
        File f = new File(file.getText());
        System.out.println("File : " + file.getText());
        System.out.println("Adding file: " + f.getAbsolutePath());
        if (f.exists()) {
            System.out.println("File exists");
            SeedManager.getInstance().addSeed(f, 64);
        }
    }
}
