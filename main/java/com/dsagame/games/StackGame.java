package com.dsagame.games;

import com.dsagame.GameState;
import com.dsagame.ui.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Stack Mini-Game.
 */
public class StackGame extends JPanel {

    private static final Color BG       = new Color(18, 18, 35);
    private static final Color CARD_BG  = new Color(30, 30, 55);
    private static final Color ACCENT   = new Color(99, 102, 241);
    private static final Color CORRECT  = new Color(34, 197, 94);
    private static final Color WRONG    = new Color(239, 68, 68);

    private final JFrame frame;
    private final MainMenuPanel menu;
    private final GameState state;

    private final Deque<Integer> playerStack = new ArrayDeque<>();
    private final Deque<Integer> targetStack = new ArrayDeque<>();
    private final java.util.List<Integer> targetList = new ArrayList<>();

    private int wrongMoves = 0;
    private int totalMoves = 0;

    private JTextField inputField;
    private JLabel statusLabel;
    private JPanel playerPanel;
    private JPanel targetPanel;

    public StackGame(JFrame frame, MainMenuPanel menu, GameState state) {
        this.frame = frame;
        this.menu  = menu;
        this.state = state;
        generateTarget();
        buildUI();
    }

    private void generateTarget() {
        targetList.clear();
        targetStack.clear();
        int size = 3 + new Random().nextInt(3);
        Random rng = new Random();
        for (int i = 0; i < size; i++) {
            int v = rng.nextInt(20) + 1;
            targetList.add(v);
            targetStack.push(v);
        }
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Stack Challenge", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(226, 232, 240));
        add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(1, 3, 20, 0));
        centre.setBackground(BG);

        playerPanel = buildStackPanel("Your Stack", playerStack, false);
        centre.add(playerPanel);

        JPanel controls = buildControls();
        centre.add(controls);

        targetPanel = buildStackPanel("Target Stack", null, true);
        centre.add(targetPanel);

        add(centre, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 6));
        south.setBackground(BG);

        statusLabel = new JLabel("Push values to build your stack to match the target!", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(148, 163, 184));
        south.add(statusLabel, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnRow.setBackground(BG);
        
        JButton backBtn = new JButton("< Menu");
        styleBtn(backBtn, new Color(71, 85, 105));
        backBtn.addActionListener(e -> menu.returnToMenu(-1, 0));
        
        JButton tutorialBtn = new JButton("Tutorial");
        styleBtn(tutorialBtn, new Color(16, 185, 129));
        tutorialBtn.addActionListener(e -> showTutorial());
        
        JButton resetBtn = new JButton("New Challenge");
        styleBtn(resetBtn, new Color(245, 158, 11));
        resetBtn.addActionListener(e -> {
            playerStack.clear();
            wrongMoves = 0; totalMoves = 0;
            generateTarget();
            refreshPanels();
            statusLabel.setText("New challenge! Build the target stack.");
        });
        
        btnRow.add(backBtn);
        btnRow.add(tutorialBtn);
        btnRow.add(resetBtn);
        south.add(btnRow, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);
    }

    private JPanel buildControls() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        JLabel lbl = new JLabel("Value to push:");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputField = new JTextField(6);
        inputField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setMaximumSize(new Dimension(120, 40));
        inputField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton pushBtn = new JButton("PUSH");
        styleBtn(pushBtn, ACCENT);
        pushBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        pushBtn.addActionListener(e -> doPush());
        inputField.addActionListener(e -> doPush());

        JButton popBtn = new JButton("POP");
        styleBtn(popBtn, new Color(239, 68, 68));
        popBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        popBtn.addActionListener(e -> doPop());

        JButton checkBtn = new JButton("Check Target");
        styleBtn(checkBtn, CORRECT);
        checkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBtn.addActionListener(e -> checkWin());

        p.add(Box.createVerticalGlue());
        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(inputField);
        p.add(Box.createVerticalStrut(14));
        p.add(pushBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(popBtn);
        p.add(Box.createVerticalStrut(24));
        p.add(checkBtn);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private void doPush() {
        String txt = inputField.getText().trim();
        try {
            int val = Integer.parseInt(txt);
            if (val < 1 || val > 99) { statusLabel.setText("Enter a value between 1-99."); return; }
            playerStack.push(val);
            inputField.setText("");
            totalMoves++;
            statusLabel.setText("Pushed " + val + " onto the stack.");
            refreshPanels();
        } catch (NumberFormatException e) {
            statusLabel.setText("Enter a valid number.");
        }
    }

    private void doPop() {
        if (playerStack.isEmpty()) {
            statusLabel.setText("Stack is empty - nothing to pop!");
            wrongMoves++;
            return;
        }
        int val = playerStack.pop();
        totalMoves++;
        statusLabel.setText("Popped " + val + " from the stack.");
        refreshPanels();
    }

    private void checkWin() {
        Deque<Integer> pCopy = new ArrayDeque<>(playerStack);
        Deque<Integer> tCopy = new ArrayDeque<>(targetStack);

        if (pCopy.size() != tCopy.size()) {
            statusLabel.setText("Stack sizes don't match. Target has " + tCopy.size() + " elements.");
            wrongMoves++;
            return;
        }
        boolean match = true;
        while (!pCopy.isEmpty()) {
            if (!pCopy.pop().equals(tCopy.pop())) { match = false; break; }
        }
        if (match) {
            int score = Math.max(0, 150 - wrongMoves * 15);
            JOptionPane.showMessageDialog(frame,
                "Stack matches the target!\n\nTotal moves: " + totalMoves +
                "\nWrong moves: " + wrongMoves +
                "\n\nPoints earned: +" + score,
                "Stack Complete!", JOptionPane.INFORMATION_MESSAGE);
            menu.returnToMenu(1, score);
        } else {
            wrongMoves++;
            statusLabel.setText("Stacks don't match. Check top-to-bottom order!");
        }
    }

    private void showTutorial() {
        String msg = "<html><body style='width: 300px;'>" +
            "<h3>Stack Tutorial</h3>" +
            "<p><b>How it works:</b> A Stack follows the LIFO (Last In, First Out) principle. Think of it like a stack of plates: you can only add (PUSH) a plate to the top, and remove (POP) a plate from the top.</p>" +
            "<p><b>How to play:</b> Enter a number and click PUSH to add it to your stack, or POP to remove the top number. Replicate the Target Stack exactly from bottom to top!</p>" +
            "</body></html>";
        JOptionPane.showMessageDialog(frame, msg, "Tutorial", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshPanels() {
        Container parent = playerPanel.getParent();
        int idx = -1;
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == playerPanel) { idx = i; break; }
        }
        playerPanel = buildStackPanel("Your Stack", playerStack, false);
        if (idx >= 0) parent.remove(idx);
        if (idx >= 0) parent.add(playerPanel, idx);
        parent.revalidate(); parent.repaint();
    }

    private JPanel buildStackPanel(String title, Deque<Integer> stack, boolean isTarget) {
        JPanel outer = new JPanel(new BorderLayout(0, 8));
        outer.setBackground(BG);

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLbl.setForeground(new Color(226, 232, 240));
        outer.add(titleLbl, BorderLayout.NORTH);

        JPanel stackBox = new JPanel();
        stackBox.setLayout(new BoxLayout(stackBox, BoxLayout.Y_AXIS));
        stackBox.setBackground(CARD_BG);
        stackBox.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 90)));

        java.util.List<Integer> items;
        if (isTarget) {
            items = new ArrayList<>(targetList);
        } else {
            items = new ArrayList<>(stack);
        }

        if (items.isEmpty()) {
            JLabel empty = new JLabel("(empty)", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(new Color(100, 116, 139));
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            stackBox.add(Box.createVerticalGlue());
            stackBox.add(empty);
            stackBox.add(Box.createVerticalGlue());
        } else {
            stackBox.add(Box.createVerticalGlue());
            boolean first = true;
            for (int v : items) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
                cell.setBackground(isTarget ? new Color(30, 80, 60) : new Color(40, 40, 80));
                cell.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(55, 55, 90)));

                JLabel valLbl = new JLabel(String.valueOf(v), SwingConstants.CENTER);
                valLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                valLbl.setForeground(isTarget ? CORRECT : new Color(165, 180, 252));
                cell.add(valLbl, BorderLayout.CENTER);

                if (first) {
                    JLabel topTag = new JLabel(" <- TOP", SwingConstants.LEFT);
                    topTag.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    topTag.setForeground(new Color(148, 163, 184));
                    cell.add(topTag, BorderLayout.EAST);
                    first = false;
                }
                stackBox.add(cell);
            }
        }

        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(40, 40, 70));
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel bottomLbl = new JLabel("- BOTTOM -", SwingConstants.CENTER);
        bottomLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bottomLbl.setForeground(new Color(100, 116, 139));
        bottom.add(bottomLbl);
        stackBox.add(bottom);

        outer.add(stackBox, BorderLayout.CENTER);
        return outer;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setMaximumSize(new Dimension(160, 40));
    }
}
