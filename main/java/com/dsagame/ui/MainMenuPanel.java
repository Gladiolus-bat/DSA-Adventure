package com.dsagame.ui;

import com.dsagame.GameState;
import com.dsagame.games.BubbleSortGame;
import com.dsagame.games.StackGame;
import com.dsagame.games.QueueGame;
import com.dsagame.games.BinarySearchGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Main menu with 4 mini-game buttons, score display, and lives.
 */
public class MainMenuPanel extends JPanel {

    private final JFrame frame;
    private final GameState state;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JButton[] gameButtons;
    private JLabel[] statusLabels;

    private static final Color BG         = new Color(18, 18, 35);
    private static final Color ACCENT      = new Color(99, 102, 241);
    private static final Color DONE_COLOR  = new Color(34, 197, 94);
    private static final Color CARD_BG    = new Color(30, 30, 55);
    private static final Color TEXT_COLOR = new Color(226, 232, 240);

    private static final String[] TITLES = {
        "Bubble Sort",
        "Stack Challenge",
        "Queue Challenge",
        "Binary Search"
    };
    private static final String[] DESCS = {
        "Swap adjacent bars to sort the array!",
        "Push & pop to match the target stack!",
        "Enqueue & dequeue tasks in order!",
        "Guess which half hides the target!"
    };

    public MainMenuPanel(JFrame frame, GameState state) {
        this.frame = frame;
        this.state = state;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(BorderFactory.createEmptyBorder(24, 30, 10, 30));

        JLabel title = new JLabel("DSA Adventure");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(ACCENT);
        header.add(title, BorderLayout.WEST);

        JPanel hud = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        hud.setBackground(BG);
        livesLabel  = hudLabel("Lives: 3");
        scoreLabel  = hudLabel("Score: 0");
        hud.add(livesLabel);
        hud.add(scoreLabel);
        header.add(hud, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Subtitle ────────────────────────────────────────────────────────
        JLabel sub = new JLabel("Choose a mini-game to play:", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(new Color(148, 163, 184));
        sub.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(sub, BorderLayout.CENTER);

        // ── Game Cards ──────────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(BG);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));

        gameButtons  = new JButton[4];
        statusLabels = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            JPanel card = buildCard(idx);
            grid.add(card);
        }

        add(grid, BorderLayout.CENTER);

        // ── Footer ──────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 8));
        footer.setBackground(BG);

        JButton resetBtn = new JButton("Reset Game");
        styleButton(resetBtn, new Color(239, 68, 68));
        resetBtn.addActionListener(e -> {
            state.reset();
            refreshHUD();
            for (int i = 0; i < 4; i++) refreshCard(i);
        });
        footer.add(resetBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel buildCard(int idx) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 90), 1),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel titleLbl = new JLabel(TITLES[idx]);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLbl.setForeground(TEXT_COLOR);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html>" + DESCS[idx] + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(148, 163, 184));
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        statusLabels[idx] = new JLabel("[ Not Played ]");
        statusLabels[idx].setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabels[idx].setForeground(new Color(100, 116, 139));
        statusLabels[idx].setAlignmentX(Component.LEFT_ALIGNMENT);

        gameButtons[idx] = new JButton("Play");
        styleButton(gameButtons[idx], ACCENT);
        gameButtons[idx].setAlignmentX(Component.LEFT_ALIGNMENT);
        gameButtons[idx].addActionListener((ActionEvent e) -> launchGame(idx));

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(descLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabels[idx]);
        card.add(Box.createVerticalStrut(12));
        card.add(gameButtons[idx]);
        return card;
    }

    private void launchGame(int idx) {
        JPanel gamePanel = switch (idx) {
            case 0 -> new BubbleSortGame(frame, this, state);
            case 1 -> new StackGame(frame, this, state);
            case 2 -> new QueueGame(frame, this, state);
            case 3 -> new BinarySearchGame(frame, this, state);
            default -> throw new IllegalStateException();
        };
        frame.setContentPane(gamePanel);
        frame.revalidate();
        frame.repaint();
    }

    public void returnToMenu(int completedIdx, int pointsEarned) {
        if (completedIdx >= 0) {
            state.setCompleted(completedIdx);
            state.addScore(pointsEarned);
        }
        refreshHUD();
        for (int i = 0; i < 4; i++) refreshCard(i);
        frame.setContentPane(this);
        frame.revalidate();
        frame.repaint();

        if (state.completedCount() == 4) {
            JOptionPane.showMessageDialog(frame,
                "You completed ALL mini-games!\n\nFinal Score: " + state.getTotalScore(),
                "DSA Master!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshHUD() {
        scoreLabel.setText("Score: " + state.getTotalScore());
        livesLabel.setText("Lives: " + state.getLives());
    }

    private void refreshCard(int idx) {
        if (state.isCompleted(idx)) {
            statusLabels[idx].setText("[ Completed ]");
            statusLabels[idx].setForeground(DONE_COLOR);
            gameButtons[idx].setText("Replay");
        }
    }

    private JLabel hudLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(TEXT_COLOR);
        return l;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}
