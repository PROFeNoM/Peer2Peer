package peer.gui;

import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

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
            GridLayout gd = new GridLayout(3, 2);
            JTextField port = new JTextField();
            JTextField ip = new JTextField();
            dialog.setLayout(gd);
            dialog.add(new JLabel("Port number"));
            dialog.add(port);
            dialog.add(new JLabel("IP address"));
            dialog.add(ip);
            int result = JOptionPane.showConfirmDialog(null, dialog, "Enter the server infos", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    pb.startPeer(port.getText(), ip.getText());
                    pb.refreshLabel("Running");
                } catch (Exception NumberFormatException) {
                    JFrame jframe = new JFrame();
                    if ((port.getText().isEmpty()) || (ip.getText().isEmpty())) {
                        JOptionPane.showMessageDialog(jframe, "Cannot process your request with no input data");
                    }
                    else {
                        JOptionPane.showMessageDialog(jframe, "Cannot process your request with invalid input data");
                    }
                }
            } else if (result == JOptionPane.CANCEL_OPTION) {
                pb.refreshLabel("Stopped");
            }
        } else if (e.getActionCommand().equals("Remove file(s)")) {
            System.out.println("Removing file");
            File f = new File("seeds");
            String[] files = f.list();
            String FileToRemove = (String) JOptionPane.showInputDialog(
                null,
                "What file do you want to remove ?",
                "Choose file",
                JOptionPane.QUESTION_MESSAGE,
                null,
                files,
                null);
            pb.removeFile(FileToRemove);
            System.out.println("File removed");
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
        } else {
            System.out.println("Unknown button pressed");
        }
    }
}