package peer.gui;

import javax.swing.JLabel;

public class FileLabel extends JLabel {
    private String key;
    private String fileName;

    public FileLabel(String text) {
        super(text);
        this.fileName = text;
    }

    public FileLabel(String text, String key) {
        super(text);
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
