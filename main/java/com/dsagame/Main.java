package com.dsagame;

import com.dsagame.ui.MainMenuPanel;
import javax.swing.*;

/**
 * Entry point for the DSA Learning Game.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("DSA Adventure 🎮");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 650);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            GameState state = new GameState();
            MainMenuPanel menu = new MainMenuPanel(frame, state);
            frame.setContentPane(menu);
            frame.setVisible(true);
        });
    }
}
