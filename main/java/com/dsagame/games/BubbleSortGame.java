package com.dsagame.games;

import com.dsagame.GameState;
import com.dsagame.ui.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Bubble Sort Mini-Game.
 */
public class BubbleSortGame extends JPanel {

    private static final int N = 7;
    private static final Color BG      = new Color(18, 18, 35);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color CORRECT = new Color(34, 197, 94);
    private static final Color WRONG   = new Color(239, 68, 68);
    private static final Color BAR_DEFAULT = new Color(99, 102, 241);
    private static final Color BAR_SELECTED = new Color(251, 191, 36);
    private static final Color BAR_SORTED   = new Color(34, 197, 94);

    private final JFrame frame;
    private final MainMenuPanel menu;
    private final GameState state;

    private int[] values;
    private boolean[] sorted;
    private int selectedBar = -1;
    private int wrongMoves  = 0;
    private int correctMoves = 0;
    private JLabel statusLabel;
    private JLabel moveLabel;
    private JPanel barPanel;
    private Timer flashTimer;
    private Color flashColor = null;

    public BubbleSortGame(JFrame frame, MainMenuPanel menu, GameState state) {
        this.frame = frame;
        this.menu  = menu;
        this.state = state;
        initGame();
        buildUI();
    }

    private void initGame() {
        values = new int[N];
        sorted = new boolean[N];
        Random rng = new Random();
        do {
            for (int i = 0; i < N; i++) values[i] = rng.nextInt(9) + 1;
        } while (isSorted());
        checkSorted();
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Bubble Sort Challenge", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(226, 232, 240));
        add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout(0, 8));
        centre.setBackground(BG);

        JLabel instrLbl = new JLabel(
            "<html><center>Click a bar, then click an adjacent bar to swap.<br>"
            + "Only valid bubble-sort swaps are accepted.</center></html>",
            SwingConstants.CENTER);
        instrLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instrLbl.setForeground(new Color(148, 163, 184));
        centre.add(instrLbl, BorderLayout.NORTH);

        barPanel = new BarPanel();
        centre.add(barPanel, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);

        JPanel hud = new JPanel(new GridLayout(1, 2, 20, 0));
        hud.setBackground(BG);
        statusLabel = hudLabel("Select a bar to begin!");
        moveLabel   = hudLabel("Moves - Correct: 0 | Wrong: 0");
        hud.add(statusLabel);
        hud.add(moveLabel);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setBackground(BG);

        JButton backBtn = new JButton("< Menu");
        styleBtn(backBtn, new Color(71, 85, 105));
        backBtn.addActionListener(e -> menu.returnToMenu(-1, 0));

        JButton tutorialBtn = new JButton("Tutorial");
        styleBtn(tutorialBtn, new Color(16, 185, 129));
        tutorialBtn.addActionListener(e -> showTutorial());

        JButton resetBtn = new JButton("New Array");
        styleBtn(resetBtn, new Color(245, 158, 11));
        resetBtn.addActionListener(e -> { initGame(); barPanel.repaint(); updateHUD(); });

        btnRow.add(backBtn);
        btnRow.add(tutorialBtn);
        btnRow.add(resetBtn);

        JPanel south = new JPanel(new BorderLayout(0, 6));
        south.setBackground(BG);
        south.add(hud, BorderLayout.NORTH);
        south.add(btnRow, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }

    private void handleBarClick(int idx) {
        if (sorted[idx] && selectedBar == -1) return;

        if (selectedBar == -1) {
            selectedBar = idx;
            statusLabel.setText("Bar " + values[idx] + " selected. Now click an adjacent bar.");
        } else {
            int a = Math.min(selectedBar, idx);
            int b = Math.max(selectedBar, idx);
            if (b - a == 1) {
                if (values[a] > values[b]) {
                    int tmp = values[a]; values[a] = values[b]; values[b] = tmp;
                    correctMoves++;
                    flash(CORRECT);
                    checkSorted();
                    if (isSorted()) {
                        onWin();
                    } else {
                        statusLabel.setText("Good swap! Keep going.");
                    }
                } else {
                    wrongMoves++;
                    flash(WRONG);
                    statusLabel.setText("Invalid swap: bigger bar should go right.");
                }
            } else {
                statusLabel.setText("Bars must be adjacent! Try again.");
            }
            selectedBar = -1;
        }
        updateHUD();
        barPanel.repaint();
    }

    private void checkSorted() {
        for (int i = 0; i < N - 1; i++) sorted[i] = false;
        sorted[N - 1] = true;
        for (int i = N - 1; i >= 0; i--) {
            boolean ok = true;
            for (int j = i + 1; j < N; j++) {
                if (values[j] < values[i]) { ok = false; break; }
            }
            sorted[i] = ok;
        }
    }

    private boolean isSorted() {
        for (int i = 0; i < N - 1; i++) if (values[i] > values[i + 1]) return false;
        return true;
    }

    private void onWin() {
        int score = Math.max(0, 200 - wrongMoves * 20);
        for (int i = 0; i < N; i++) sorted[i] = true;
        barPanel.repaint();
        JOptionPane.showMessageDialog(frame,
            "Array Sorted!\n\nCorrect swaps: " + correctMoves +
            "\nWrong attempts: " + wrongMoves +
            "\n\nPoints earned: +" + score,
            "Bubble Sort Complete!", JOptionPane.INFORMATION_MESSAGE);
        menu.returnToMenu(0, score);
    }

    private void flash(Color c) {
        flashColor = c;
        barPanel.repaint();
        if (flashTimer != null) flashTimer.stop();
        flashTimer = new Timer(400, e -> { flashColor = null; barPanel.repaint(); });
        flashTimer.setRepeats(false);
        flashTimer.start();
    }

    private void updateHUD() {
        moveLabel.setText("Moves - Correct: " + correctMoves + " | Wrong: " + wrongMoves);
    }

    private void showTutorial() {
        String msg = "<html><body style='width: 300px;'>" +
            "<h3>Bubble Sort Tutorial</h3>" +
            "<p><b>How it works:</b> Bubble Sort is a simple algorithm that repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order. The largest elements \"bubble\" to the end of the list.</p>" +
            "<p><b>How to play:</b> Click a bar, then click an adjacent bar. You must only make swaps that move a larger bar to the right. Repeat this until the whole array is sorted!</p>" +
            "</body></html>";
        JOptionPane.showMessageDialog(frame, msg, "Tutorial", JOptionPane.INFORMATION_MESSAGE);
    }

    private class BarPanel extends JPanel {
        BarPanel() {
            setBackground(new Color(25, 25, 45));
            setPreferredSize(new Dimension(800, 380));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int w = getWidth();
                    int barW = w / N;
                    int clicked = e.getX() / barW;
                    if (clicked >= 0 && clicked < N) handleBarClick(clicked);
                }
            });
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int barW = w / N - 4;
            int maxVal = 9;

            if (flashColor != null) {
                g2.setColor(new Color(flashColor.getRed(), flashColor.getGreen(), flashColor.getBlue(), 40));
                g2.fillRect(0, 0, w, h);
            }

            for (int i = 0; i < N; i++) {
                int barH = (int)((values[i] / (double) maxVal) * (h - 80));
                int x = i * (w / N) + 2;
                int y = h - barH - 40;

                Color c;
                if (sorted[i])        c = BAR_SORTED;
                else if (i == selectedBar) c = BAR_SELECTED;
                else                   c = BAR_DEFAULT;

                g2.setColor(c);
                g2.fillRoundRect(x, y, barW, barH, 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2.getFontMetrics();
                String val = String.valueOf(values[i]);
                int tx = x + (barW - fm.stringWidth(val)) / 2;
                g2.drawString(val, tx, y - 8);

                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString("[" + i + "]", x + barW/2 - 8, h - 20);
            }
        }
    }

    private JLabel hudLabel(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(new Color(226, 232, 240));
        return l;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }
}
