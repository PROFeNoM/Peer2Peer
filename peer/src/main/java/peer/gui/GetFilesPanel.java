package peer.gui;

import peer.Peer;
import peer.seed.SeedManager;
import peer.seed.Seed;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JOptionPane;
import java.awt.Color;

public class GetFilesPanel extends JPanel {
    public ArrayList<String> files;
    private Peer peer;

    public GetFilesPanel(Peer p) {
        super();
        this.peer = p;
        this.files = new ArrayList<String>();
        ArrayList<Seed> seeds = SeedManager.getInstance().getSeeds();
        for (Seed s : seeds) {
            this.files.add(s.getName());
            JPanel jl = new JPanel();
            FileLabel lab = new FileLabel(s.getName(), s.getKey());
            jl.add(lab);
            lab.addMouseListener(new PanelListener(lab, peer));
            this.add(jl);
        }
    }

    public void addFile(String fileName) {
        System.out.println("Previous files : " + files);
        if (!files.contains(fileName)) {
            files.add(fileName);
            System.out.println("Added file: " + fileName);
        }
        System.out.println("New files : " + files);
    }

    public void removeFile(String fileName) {
        if (files.contains(fileName)) {
            files.remove(fileName);
        } else {
            JOptionPane.showMessageDialog(null, this.getParent(), "No such file to remove", JOptionPane.OK_OPTION);
        }
    }

    public void refresh() {
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
        this.setBackground(Color.GREEN);
        this.setOpaque(true);
        this.revalidate();
        this.repaint();
    }
}
