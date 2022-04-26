package peer.src.main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PeerButtonListener implements ActionListener {
    private String text;

    public PeerButtonListener(String text) {
        this.text = text;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PeerButton pb = (PeerButton) e.getSource();
        if (text.equals("Stop")) {
            System.out.println("Stopping peer");
            pb.stopPeer();
        } else if (e.getActionCommand().equals("Start")) {
            System.out.println("Starting peer");

            pb.startPeer();
        } else if (e.getActionCommand().equals("Add file")) {
            System.out.println("Adding file");
            pb.addFile();
        } else {
            System.out.println("Unknown button pressed");
        }
    }
}