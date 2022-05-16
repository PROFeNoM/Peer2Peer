package peer.gui;

import peer.Peer;
import peer.seed.SeedManager;
import peer.seed.Seed;

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

public class GetFilesPanel extends JPanel {
    ArrayList<String> files;
    Peer peer;

 
    public GetFilesPanel() {
        super();
        this.files = new ArrayList<String>();
        ArrayList<Seed> seeds = SeedManager.getInstance().getSeeds();
        for (Seed s : seeds) {
            this.files.add(s.getName());
        }
        for (String file : files) {
            JPanel jl = new JPanel();
            jl.add(new JLabel(file));
            this.add(jl);
        }
    }

    public void addFile(String fileName) {
        // TODO Auto-generated method stub
        System.out.println("Previous files : " + files);
        if (!files.contains(fileName)) {
            files.add(fileName);
            System.out.println("Added file: " + fileName);
        }
        System.out.println("New files : " + files);
    }

    public void removeFile(String fileName) {
        // TODO Auto-generated method stub
        if (files.contains(fileName)) {
            files.remove(fileName);
        } else {
            JOptionPane.showMessageDialog(null, this.getParent(), "No such file to remove", JOptionPane.OK_OPTION);
        }
    }

    public void refresh() {
        // TODO Auto-generated method stub
        SeedManager seedManager = SeedManager.getInstance();
        ArrayList<Seed> tmpFiles = seedManager.getSeeds();
        for (Seed seed : tmpFiles) {
            if (!files.contains(seed.getName())) {
                files.add(seed.getName());
            }
        }

        JPanel dialog = new JPanel();
        for (String file : files) {
            JPanel jl = new JPanel();
            jl.add(new JLabel(file));
            dialog.add(jl);
        }
        int result = JOptionPane.showConfirmDialog(null, dialog, "Seeds : ", JOptionPane.OK_CANCEL_OPTION);
        


        System.out.println("Refreshed files");
        this.repaint();
        this.revalidate();

    }
}
