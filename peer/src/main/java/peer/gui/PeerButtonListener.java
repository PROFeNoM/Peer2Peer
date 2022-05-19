package peer.gui;

import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.io.File;
import java.lang.module.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.*;
import static java.nio.file.LinkOption.*;

import java.awt.event.ActionEvent;

public class PeerButtonListener implements ActionListener {
    private String text;
    private ConnectedPanel connectedPanel;

    public PeerButtonListener(String text) {
        this.text = text;
    }

    public PeerButtonListener(String text, ConnectedPanel connectedPanel) {
        this.text = text;
        this.connectedPanel = connectedPanel;
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
            JTextField ipTracker = new JTextField("127.0.0.1");
            JTextField portTracker = new JTextField("1234");
            dialog.setLayout(gd);
            dialog.add(new JLabel("Port peer"));
            dialog.add(portPeer);
            dialog.add(new JLabel("IP Tracker"));
            dialog.add(ipTracker);
            dialog.add(new JLabel("Port Tracker"));
            dialog.add(portTracker);
            int result = JOptionPane.showConfirmDialog(null, dialog, "Enter the tracker infos",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    pb.startPeer(ipTracker.getText(), Integer.parseInt(portTracker.getText()),
                            Integer.parseInt(portPeer.getText()));
                    pb.refreshLabel("Running");
                } catch (Exception NumberFormatException) {
                    JFrame jframe = new JFrame();
                    if ((portPeer.getText().isEmpty())
                            || (ipTracker.getText().isEmpty() || (portTracker.getText().isEmpty()))) {
                        JOptionPane.showMessageDialog(jframe, "Cannot process your request with no input data");
                    } else {
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
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);
            // TODO : uncomment when théo pushed
            // String dir = Configuration.getInstance.getStoragePath();
            // fc.setCurrentDirectory(new File(dir));
            File[] files = fc.getSelectedFiles();
            for (File file : files) {

                // TODO : Suppr when théo pushed
                fc.setCurrentDirectory(new File("seeds"));
                fc.showDialog(pb.getParent(), "Add file(s)");

                pb.addFile(file.getName());

            }
            // connectedPanel.refresh();
        } else if (e.getActionCommand().equals("Swap"))

        {

        } else {
            System.out.println("Unknown button pressed");
        }
    }
}