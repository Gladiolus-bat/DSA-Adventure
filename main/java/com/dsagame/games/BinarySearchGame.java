package com.dsagame.games;

import com.dsagame.GameState;
import com.dsagame.ui.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Binary Search Mini-Game.
 */
public class BinarySearchGame extends JPanel {

    private static final int N = 15;
    private static final Color BG       = new Color(18, 18, 35);
    private static final Color CARD_BG  = new Color(30, 30, 55);
    private static final Color ACCENT   = new Color(99, 102, 241);
    private static final Color CORRECT  = new Color(34, 197, 94);
    private static final Color WRONG    = new Color(239, 68, 68);
    private static final Color RANGE    = new Color(251, 191, 36);
    private static final Color MID_CLR  = new Color(236, 72, 153);

    private final JFrame frame;
    private final MainMenuPanel menu;
    private final GameState state;

    private int[] arr;
    private int target;
    private int low, high, mid;
    private int steps = 0;
    private int optimalSteps;
    private boolean found = false;
    private int wrongGuesses = 0;

    private JLabel targetLabel;
    private JLabel rangeLabel;
    private JLabel stepLabel;
    private JLabel hintLabel;
    private JPanel arrayPanel;

    public BinarySearchGame(JFrame frame, MainMenuPanel menu, GameState state) {
        this.frame = frame;
        this.menu  = menu;
        this.state = state;
        initGame();
        buildUI();
    }

    private void initGame() {
        arr = new int[N];
        Random rng = new Random();
        arr[0] = rng.nextInt(5) + 1;
        for (int i = 1; i < N; i++) arr[i] = arr[i - 1] + rng.nextInt(8) + 1;

        target = arr[rng.nextInt(N)];
        low = 0; high = N - 1;
        mid = (low + high) / 2;
        steps = 0; found = false; wrongGuesses = 0;

        optimalSteps = (int) Math.ceil(Math.log(N) / Math.log(2));
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Binary Search Challenge", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(226, 232, 240));
        add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout(0, 16));
        centre.setBackground(BG);

        JPanel infoBox = new JPanel(new GridLayout(4, 1, 0, 4));
        infoBox.setBackground(CARD_BG);
        infoBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 55, 90)),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        targetLabel = infoLabel("Target: " + target);
        rangeLabel  = infoLabel("Search Range: [" + low + " ... " + high + "]  |  Mid index: " + mid + "  (value: " + arr[mid] + ")");
        stepLabel   = infoLabel("Steps Taken: 0  |  Optimal: " + optimalSteps);
        hintLabel   = infoLabel("Is your target in the LEFT half or RIGHT half of the remaining range?");

        infoBox.add(targetLabel);
        infoBox.add(rangeLabel);
        infoBox.add(stepLabel);
        infoBox.add(hintLabel);
        centre.add(infoBox, BorderLayout.NORTH);

        arrayPanel = new ArrayDisplayPanel();
        centre.add(arrayPanel, BorderLayout.CENTER);

        JPanel choiceRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 8));
        choiceRow.setBackground(BG);

        JButton leftBtn = new JButton("< Left Half (target < mid)");
        styleBtn(leftBtn, new Color(6, 182, 212));
        leftBtn.addActionListener(e -> guess(true));

        JButton exactBtn = new JButton("Mid IS the Target");
        styleBtn(exactBtn, CORRECT);
        exactBtn.addActionListener(e -> guessExact());

        JButton rightBtn = new JButton("Right Half (target > mid) >");
        styleBtn(rightBtn, ACCENT);
        rightBtn.addActionListener(e -> guess(false));

        choiceRow.add(leftBtn);
        choiceRow.add(exactBtn);
        choiceRow.add(rightBtn);
        centre.add(choiceRow, BorderLayout.SOUTH);

        add(centre, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        footer.setBackground(BG);

        JButton backBtn = new JButton("< Menu");
        styleBtn(backBtn, new Color(71, 85, 105));
        backBtn.addActionListener(e -> menu.returnToMenu(-1, 0));
        
        JButton tutorialBtn = new JButton("Tutorial");
        styleBtn(tutorialBtn, new Color(16, 185, 129));
        tutorialBtn.addActionListener(e -> showTutorial());

        JButton resetBtn = new JButton("New Search");
        styleBtn(resetBtn, new Color(245, 158, 11));
        resetBtn.addActionListener(e -> {
            initGame();
            removeAll();
            buildUI();
            revalidate(); repaint();
        });

        footer.add(backBtn);
        footer.add(tutorialBtn);
        footer.add(resetBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void guess(boolean leftHalf) {
        if (found) return;
        steps++;

        int midVal = arr[mid];
        boolean correct;

        if (leftHalf) {
            correct = target < midVal;
            if (correct) high = mid - 1;
        } else {
            correct = target > midVal;
            if (correct) low = mid + 1;
        }

        if (!correct) {
            wrongGuesses++;
            hintLabel.setText("Wrong half! Think again. Target = " + target + ", mid value = " + midVal);
            steps--;
        } else {
            if (low > high) {
                hintLabel.setText("Search range exhausted. Target not found? That shouldn't happen!");
                return;
            }
            mid = (low + high) / 2;
            hintLabel.setText("Correct! New mid: index " + mid + " (value " + arr[mid] + ")");
            rangeLabel.setText("Search Range: [" + low + " ... " + high + "]  |  Mid: " + mid + "  (value: " + arr[mid] + ")");
            stepLabel.setText("Steps Taken: " + steps + "  |  Optimal: " + optimalSteps);
        }
        arrayPanel.repaint();
    }

    private void guessExact() {
        if (found) return;
        if (arr[mid] == target) {
            found = true;
            steps++;
            int score = Math.max(0, 100 + (optimalSteps - steps) * 20 - wrongGuesses * 10);
            score = Math.max(score, 50);
            arrayPanel.repaint();
            hintLabel.setText("Found " + target + " at index " + mid + "!");

            JOptionPane.showMessageDialog(frame,
                "Correct! " + target + " is at index " + mid + "!\n\n" +
                "Steps taken: " + steps + "\nOptimal steps: " + optimalSteps +
                "\nWrong guesses: " + wrongGuesses +
                "\n\nPoints earned: +" + score,
                "Binary Search Complete!", JOptionPane.INFORMATION_MESSAGE);
            menu.returnToMenu(3, score);
        } else {
            wrongGuesses++;
            hintLabel.setText("Mid value is " + arr[mid] + ", not " + target + ". Keep searching!");
        }
    }
    
    private void showTutorial() {
        String msg = "<html><body style='width: 300px;'>" +
            "<h3>Binary Search Tutorial</h3>" +
            "<p><b>How it works:</b> Binary Search finds a target in a <b>sorted</b> array by repeatedly dividing the search interval in half. If the target is less than the middle element, it narrows the search to the lower half. Otherwise, it searches the upper half.</p>" +
            "<p><b>How to play:</b> Look at the Mid pointer. If your target is smaller, click Left Half. If it's larger, click Right Half. If the mid value equals your target, click 'Mid IS the Target'!</p>" +
            "</body></html>";
        JOptionPane.showMessageDialog(frame, msg, "Tutorial", JOptionPane.INFORMATION_MESSAGE);
    }

    private class ArrayDisplayPanel extends JPanel {
        ArrayDisplayPanel() {
            setBackground(new Color(22, 22, 42));
            setPreferredSize(new Dimension(800, 200));
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int cellW = w / N;
            int cellH = 60;
            int startY = (h - cellH - 40) / 2;

            for (int i = 0; i < N; i++) {
                int x = i * cellW + 2;
                boolean inRange = (i >= low && i <= high);
                boolean isMid   = (i == mid);
                boolean isTarget = (found && i == mid);

                Color bg;
                if (isTarget)       bg = CORRECT;
                else if (isMid)     bg = MID_CLR;
                else if (inRange)   bg = RANGE;
                else                bg = new Color(40, 40, 60);

                g2.setColor(bg);
                g2.fillRoundRect(x, startY, cellW - 4, cellH, 8, 8);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                String val = String.valueOf(arr[i]);
                int tx = x + (cellW - 4 - fm.stringWidth(val)) / 2;
                g2.drawString(val, tx, startY + cellH / 2 + 5);

                g2.setColor(new Color(148, 163, 184));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                String idx = "[" + i + "]";
                int ix = x + (cellW - 4 - g2.getFontMetrics().stringWidth(idx)) / 2;
                g2.drawString(idx, ix, startY + cellH + 18);

                if (isMid) {
                    g2.setColor(MID_CLR);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2.drawString("MID", x + (cellW - 4 - g2.getFontMetrics().stringWidth("MID")) / 2, startY + cellH + 34);
                }
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            int ly = h - 14;
            drawLegend(g2, 10, ly, RANGE, "Search range");
            drawLegend(g2, 160, ly, MID_CLR, "Mid pointer");
            drawLegend(g2, 290, ly, CORRECT, "Found");
            drawLegend(g2, 380, ly, new Color(40, 40, 60), "Eliminated");
        }

        private void drawLegend(Graphics2D g2, int x, int y, Color c, String label) {
            g2.setColor(c);
            g2.fillRoundRect(x, y - 12, 14, 14, 4, 4);
            g2.setColor(new Color(148, 163, 184));
            g2.drawString(label, x + 18, y);
        }
    }

    private JLabel infoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        l.setForeground(new Color(226, 232, 240));
        return l;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
}
