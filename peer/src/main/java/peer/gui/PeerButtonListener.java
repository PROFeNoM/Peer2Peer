package peer.gui;

import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import java.io.File;
import javax.swing.JOptionPane;

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
        } else if (e.getActionCommand().equals("Remove file(s)")) {
            System.out.println("Removing file");
            JOptionPane pane = new JOptionPane();
            pb.removeFile(pane.showInputDialog("Enter file name you want to remove"));
        }else if (e.getActionCommand().equals("Add file(s)")) {
            System.out.println("Opening files");
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            fc.setCurrentDirectory(new File("seeds"));
            fc.showDialog(pb.getParent(), "Add file(s)");
            File[] files = fc.getSelectedFiles();
            for (File f : files) {
                System.out.println("File : " + f.getName());
                pb.addFile(f.getName());
            }
        } else if (e.getActionCommand().equals("Check")) {
            System.out.println("Checking seeds");
            
            pb.findSeeds();
        } else {
            System.out.println("Unknown button pressed");
        }
    }
}