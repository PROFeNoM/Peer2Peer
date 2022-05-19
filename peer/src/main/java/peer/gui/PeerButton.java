package peer.gui;

import peer.Peer;
import peer.seed.SeedManager;

import java.io.File;
import javax.swing.JLabel;
import javax.swing.JButton;

public class PeerButton extends JButton {
    private Peer peer;
    private JLabel label;

    public PeerButton(Peer p, String text) {
        super(text);
        this.peer = p;
        this.addActionListener(new PeerButtonListener(text));
    }

    public PeerButton(Peer p, String text, JLabel label) {
        super(text);
        this.peer = p;
        this.label = label;
        this.addActionListener(new PeerButtonListener(text));
    }

    public PeerButton(Peer p, String text, ConnectedPanel cp) {
        super(text);
        this.peer = p;
        this.addActionListener(new PeerButtonListener(text));
    }

    public void stopPeer() {
        peer.stop();
    }

    public void startPeer() {
        peer.start("127.0.0.1", 1234, 4321);
    }

    public void startPeer(String ipTracker, int portTracker, int portPeer) {
        peer.start(ipTracker, portTracker, portPeer);
    }

    public void addFile(String fileName) {
        File f = new File("seeds/" + fileName);
        System.out.println("File : " + fileName);
        System.out.println("Adding file: " + f.getAbsolutePath());
        if (f.exists()) {
            System.out.println("File exists");
            SeedManager.getInstance().addSeed(f, 64);
        }
    }

    public void removeFile(String fileName) {
        SeedManager.getInstance().removeSeedFromName(fileName);
        System.out.println("Removed file : " + fileName);
    }

    public void findSeeds() {
        SeedManager.getInstance().findSeeds("seeds/");
    }

    public void refreshLabel(String status) {
        label.setText("Peer interface : " + status);
        if (status.equals("Running")) {
            label.setBackground(java.awt.Color.GREEN);
        } else if (status.equals("Stopped")) {
            label.setBackground(java.awt.Color.RED);
        }
        label.setOpaque(true);
        label.repaint();
    }
}
