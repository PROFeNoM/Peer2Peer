package peer.gui;

import peer.Peer;

import javax.swing.JButton;

public class GetFilesButton extends JButton {
    private Peer peer;
    private GetFilesPanel filePanel;

    public GetFilesButton(Peer p, String text) {
        super(text);
        this.peer = p;
        this.addActionListener(new GetFilesListener(text));
    }

    public GetFilesButton(Peer p, String text, GetFilesPanel filePanel) {
        super(text);
        this.peer = p;
        this.filePanel = filePanel;
        this.addActionListener(new GetFilesListener(text, filePanel));
    }

    // Add a file from the seeds folder
    public void addFileToPanel(String fileName) {
        filePanel.addFile(fileName);
        filePanel.refresh();
    }

    public Peer getPeer() {
        return this.peer;
    }
}
