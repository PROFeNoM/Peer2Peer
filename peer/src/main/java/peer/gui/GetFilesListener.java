package peer.gui;

import peer.Peer;
import peer.seed.*;

import java.io.File;
import java.util.GregorianCalendar;

import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.event.*;

public class GetFilesListener implements ActionListener {
    private String text;
    private GetFilesPanel filePanel;

    public GetFilesListener(String text) {
        this.text = text;
    }

    public GetFilesListener(String text, GetFilesPanel filePanel) {
        this.text = text;
        this.filePanel = filePanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GetFilesButton pb = (GetFilesButton) e.getSource();
        if (text.equals("Look")) {
            Peer p = pb.getPeer();
            // Besoin de criterions (fileName, fileSize)
            JPanel dialog = new JPanel();
            GridLayout gd = new GridLayout(3, 2);
            JTextField fileName = new JTextField();
            JTextField fileSize = new JTextField();
            dialog.setLayout(gd);
            dialog.add(new JLabel("File name:"));
            dialog.add(fileName);
            dialog.add(new JLabel("File size:"));
            dialog.add(fileSize);
            int result = JOptionPane.showConfirmDialog(null, dialog, "Enter the seed infos",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String fileNameString = fileName.getText();
                String fileSizeString = fileSize.getText();
                if (fileNameString.equals("") || fileSizeString.equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a file name and a file size", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    int fileSizeInt = Integer.parseInt(fileSizeString);
                    String query = "[filename=" + fileNameString + " filesize=" + fileSizeInt + "]";
                    System.out.println(query);
                    p.look(query);
                    for (Seed s : SeedManager.getInstance().getSeeds()) {
                        System.out.println("Seed: " + s.getName() + " " + s.getSize());
                    }
                    System.out.println(fileNameString);
                    pb.addFileToPanel(fileNameString);
                }
            }
        } else if (text.equals("Look for all seeds")) {
            Peer p = pb.getPeer();
            p.look("[]");
            for (Seed s : SeedManager.getInstance().getSeeds()) {
                System.out.println();
                System.out.println("Seed: " + s.getName() + " " + s.getSize());
            }
        } else if (text.equals("Refresh")) {
            System.out.println("Refresh");
            filePanel.refresh();
        } else if (text.equals("Download")) {
            Peer peer = pb.getPeer();
            ArrayList<String> files = new ArrayList<String>();
            ArrayList<Seed> seeds = SeedManager.getInstance().getLeeches();
            JPanel dialog = new JPanel();
            for (Seed s : seeds) {
                files.add(s.getName());
                FileLabel lab = new FileLabel(s.getName(), s.getKey());
                dialog.add(lab);
                lab.addMouseListener(new PanelListener(lab, peer));
            }
            int result = JOptionPane.showConfirmDialog(null, dialog, "Choose a file to download",
                    JOptionPane.OK_CANCEL_OPTION);
        }
    }
}
