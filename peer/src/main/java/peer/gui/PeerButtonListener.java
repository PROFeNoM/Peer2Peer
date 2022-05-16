package peer.gui;

import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;

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
            pb.refreshLabel("Stopped");
            System.out.println("Stopping peer");
            pb.stopPeer();
        } else if (e.getActionCommand().equals("Start")) {
            System.out.println("Starting peer");
            JPanel dialog = new JPanel();
            GridLayout gd = new GridLayout(3, 3);
            JTextField portPeer = new JTextField();
            JTextField ipTracker = new JTextField();
            JTextField portTracker = new JTextField();
            dialog.setLayout(gd);
            dialog.add(new JLabel("Port peer"));
            dialog.add(portPeer);
            dialog.add(new JLabel("IP Tracker"));
            dialog.add(ipTracker);
            dialog.add(new JLabel("Port Tracker"));
            dialog.add(portTracker);
            int result = JOptionPane.showConfirmDialog(null, dialog, "Enter the tracker infos", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                pb.refreshLabel("Running");
                // ip tracker, port tracker, port peer
                pb.startPeer(ipTracker.getText(), Integer.parseInt(portTracker.getText()), Integer.parseInt(portPeer.getText()));
            } else if (result == JOptionPane.CANCEL_OPTION) {
                pb.refreshLabel("Stopped");
            }
        } else if (e.getActionCommand().equals("Remove file(s)")) {
            System.out.println("Removing file");
            JOptionPane pane = new JOptionPane();
            pb.removeFile(pane.showInputDialog("Enter file name you want to remove"));
        } else if (e.getActionCommand().equals("Add file(s)")) {
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
        } else if (e.getActionCommand().equals("Swap")) {
            
        } else {
            System.out.println("Unknown button pressed");
        }
    }
}