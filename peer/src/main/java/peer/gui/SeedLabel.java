package peer.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;

public class SeedLabel extends JPanel {
    
    
    public SeedLabel(String name) {
        super();
        this.setLayout(new GridLayout(1,2));
        JLabel jl = new JLabel(name);

        this.add(jl);
    }


}
