package peer.gui;

import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import java.io.File;
import peer.util.Configuration;

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
            fc.setCurrentDirectory(new File(Configuration.getInstance().getStoragePath()));
            fc.showDialog(pb.getParent(), "Add file(s)");
            File[] files = fc.getSelectedFiles();
            for (File file : files) {
                System.out.println("Selected file : " + file.getAbsolutePath());
                File tmp = new File(Configuration.getInstance().getStoragePath() + "/" + file.getName());
                System.out.println("Tmp file : " + tmp.getAbsolutePath());
                if (tmp.exists())
                    System.out.println("File already exists in directory");
                else {

                    // copy the file to the correct cirectory
                    InputStream is = null;
                    OutputStream os = null;
                    try {
                        is = new FileInputStream(file);
                        os = new FileOutputStream(Configuration.getInstance().getStoragePath() + "/" + file.getName());
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                        System.out.println("Copied file");
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } finally {
                        try {
                            is.close();
                            os.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    pb.addFile(file.getName());
                }
            }
        } else if (e.getActionCommand().equals("Swap"))

        {

        } else {
            System.out.println("Unknown button pressed");
        }
    }
}