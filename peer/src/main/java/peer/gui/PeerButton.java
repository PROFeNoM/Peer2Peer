package peer.gui;

import peer.Peer;
import peer.seed.SeedManager;

import java.io.File;
import javax.swing.JTextField;
import javax.swing.JButton;

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
        String fileName = "seeds/" + file.getText();
        File f = new File(fileName);
        System.out.println("File : " + fileName);
        System.out.println("Adding file: " + f.getAbsolutePath());
        if (f.exists()) {
            System.out.println("File exists");
            SeedManager.getInstance().addSeed(f, 64);
        }
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

    public void removeFile() {
        String fileName = file.getText();
        SeedManager.getInstance().removeSeedFromName(fileName);
        System.out.println("File : " + fileName);
    }

    public void removeFile(String fileName) {
        SeedManager.getInstance().removeSeedFromName(fileName);
        System.out.println("Removed file : " + fileName);
    }

    public void findSeeds(){
        SeedManager.getInstance().findSeeds("seeds/");
    }
}
