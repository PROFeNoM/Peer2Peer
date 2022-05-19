package peer.gui;

import peer.Peer;
import peer.seed.SeedManager;

import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class ConnectedPanel extends JPanel {
    private Peer peer;

    public ConnectedPanel(Peer p) {
        super();
        this.peer = p;

        // Second layout : Connected panel
        this.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JLabel connected = new JLabel("Peer Interface when Connected");
        topPanel.add(connected);

        // The panel where the looked files will be listed

        GetFilesPanel getFilePanel = new GetFilesPanel(this.peer);
        JPanel lookPanel = new JPanel();
        GetFilesButton look = new GetFilesButton(p, "Look", getFilePanel);
        lookPanel.add(look);
        GetFilesButton lookAll = new GetFilesButton(p, "Look for all seeds", getFilePanel);
        lookPanel.add(lookAll);
        GetFilesButton downloadButton = new GetFilesButton(p, "Download", getFilePanel);

        getFilePanel.setLayout(new FlowLayout());

        this.add(topPanel, BorderLayout.NORTH);
        this.add(getFilePanel);
        this.add(lookPanel, BorderLayout.SOUTH);
        this.add(downloadButton, BorderLayout.EAST);
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
}
