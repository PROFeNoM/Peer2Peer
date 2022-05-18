package peer.gui;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import peer.Peer;
import peer.seed.SeedManager;
import peer.seed.Seed;

import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

public class PanelListener implements MouseListener {
    FileLabel label;
    Peer peer;

    public PanelListener(FileLabel label, Peer peer) {
        super();
        this.peer = peer;
        this.label = label;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Faire un JPanel personalisé avec en background la clé de la seed de stocké pour le label
        // TODO Auto-generated method stub
        String key = label.getKey();
        JPanel dialog = new JPanel();
        JLabel labell = new JLabel("Download " + label.getText() + " ?");
        dialog.add(labell);
        int result = JOptionPane.showConfirmDialog(null, dialog, "Do you want to download this file ?", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            peer.getFile(key);
        }
        label.setBackground(Color.GREEN); 
        label.setOpaque(true);
        label.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        // System.out.println("Mouse entered");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        // System.out.println("Mouse exited");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        // System.out.println("Mouse pressed");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        // System.out.println("Mouse released");
    }
}
