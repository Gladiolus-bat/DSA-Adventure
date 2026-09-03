package com.dsagame.games;

import com.dsagame.GameState;
import com.dsagame.ui.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Queue Mini-Game.
 */
public class QueueGame extends JPanel {

    private static final Color BG      = new Color(18, 18, 35);
    private static final Color CARD_BG = new Color(30, 30, 55);
    private static final Color ACCENT  = new Color(99, 102, 241);
    private static final Color CORRECT = new Color(34, 197, 94);
    private static final Color WRONG   = new Color(239, 68, 68);

    private static final Color[] TICKET_COLORS = {
        new Color(239, 68, 68),
        new Color(245, 158, 11),
        new Color(16, 185, 129),
        new Color(99, 102, 241),
        new Color(236, 72, 153),
        new Color(6, 182, 212),
    };

    private final JFrame frame;
    private final MainMenuPanel menu;
    private final GameState state;

    private java.util.List<String> targetOrder = new ArrayList<>();
    private java.util.List<String> availableItems = new ArrayList<>();
    private final Queue<String> playerQueue = new LinkedList<>();
    private final java.util.List<String> dequeued = new ArrayList<>();

    private int wrongMoves = 0;
    private JLabel statusLabel;
    private JPanel queueDisplayPanel;
    private JPanel availablePanel;
    private JPanel dequeuedPanel;

    public QueueGame(JFrame frame, MainMenuPanel menu, GameState state) {
        this.frame = frame;
        this.menu  = menu;
        this.state = state;
        generateChallenge();
        buildUI();
    }

    private void generateChallenge() {
        String[] tasks = {"Task-A", "Task-B", "Task-C", "Task-D", "Task-E"};
        targetOrder.clear(); availableItems.clear(); playerQueue.clear(); dequeued.clear();

        java.util.List<String> all = new ArrayList<>(Arrays.asList(tasks));
        Collections.shuffle(all);
        int size = 3 + new Random().nextInt(2);
        targetOrder.addAll(all.subList(0, size));

        availableItems = new ArrayList<>(targetOrder);
        Collections.shuffle(availableItems);
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel title = new JLabel("Queue Challenge", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(226, 232, 240));
        add(title, BorderLayout.NORTH);

        JLabel instr = new JLabel(
            "<html><center>Enqueue tasks in the correct order so they are dequeued (FIFO) matching the target sequence.</center></html>",
            SwingConstants.CENTER);
        instr.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        instr.setForeground(new Color(148, 163, 184));

        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setBackground(BG);
        main.add(instr, BorderLayout.NORTH);

        JPanel targetPanel = buildLabeledRow("Target Dequeue Order (Left = First):", targetOrder, new Color(34, 197, 94));
        main.add(targetPanel, BorderLayout.NORTH);

        JPanel rows = new JPanel(new GridLayout(3, 1, 0, 12));
        rows.setBackground(BG);

        availablePanel = buildLabeledRowPanel("Available Items (Click to Enqueue):", availableItems, ACCENT);
        rows.add(availablePanel);

        queueDisplayPanel = buildQueueDisplayPanel();
        rows.add(queueDisplayPanel);

        dequeuedPanel = buildLabeledRowPanel("Dequeued Output:", dequeued, CORRECT);
        rows.add(dequeuedPanel);

        main.add(rows, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 6));
        south.setBackground(BG);

        statusLabel = new JLabel("Click an available item to enqueue it, then use DEQUEUE to process.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(148, 163, 184));
        south.add(statusLabel, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setBackground(BG);

        JButton backBtn = new JButton("< Menu");
        styleBtn(backBtn, new Color(71, 85, 105));
        backBtn.addActionListener(e -> menu.returnToMenu(-1, 0));

        JButton tutorialBtn = new JButton("Tutorial");
        styleBtn(tutorialBtn, new Color(16, 185, 129));
        tutorialBtn.addActionListener(e -> showTutorial());

        JButton deqBtn = new JButton("DEQUEUE");
        styleBtn(deqBtn, new Color(239, 68, 68));
        deqBtn.addActionListener(e -> doDequeue());

        JButton checkBtn = new JButton("Check Output");
        styleBtn(checkBtn, CORRECT);
        checkBtn.addActionListener(e -> checkWin());

        JButton resetBtn = new JButton("New Challenge");
        styleBtn(resetBtn, new Color(245, 158, 11));
        resetBtn.addActionListener(e -> resetGame());

        btnRow.add(backBtn);
        btnRow.add(tutorialBtn);
        btnRow.add(deqBtn);
        btnRow.add(checkBtn);
        btnRow.add(resetBtn);
        south.add(btnRow, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);
    }

    private void doEnqueue(String item) {
        playerQueue.offer(item);
        availableItems.remove(item);
        statusLabel.setText("Enqueued: " + item);
        refresh();
    }

    private void doDequeue() {
        if (playerQueue.isEmpty()) {
            statusLabel.setText("Queue is empty! Enqueue items first.");
            wrongMoves++;
            return;
        }
        String item = playerQueue.poll();
        dequeued.add(item);
        statusLabel.setText("Dequeued: " + item);
        refresh();

        if (dequeued.size() == targetOrder.size() && availableItems.isEmpty()) {
            checkWin();
        }
    }

    private void checkWin() {
        if (dequeued.equals(targetOrder)) {
            int score = Math.max(0, 150 - wrongMoves * 15);
            JOptionPane.showMessageDialog(frame,
                "Queue output matches the target!\n\nWrong moves: " + wrongMoves +
                "\n\nPoints earned: +" + score,
                "Queue Complete!", JOptionPane.INFORMATION_MESSAGE);
            menu.returnToMenu(2, score);
        } else {
            wrongMoves++;
            statusLabel.setText("Output doesn't match target. Check the FIFO order!");
        }
    }

    private void showTutorial() {
        String msg = "<html><body style='width: 300px;'>" +
            "<h3>Queue Tutorial</h3>" +
            "<p><b>How it works:</b> A Queue follows the FIFO (First In, First Out) principle. Think of a line of people waiting: the first person to join the line (ENQUEUE) is the first person to leave it (DEQUEUE).</p>" +
            "<p><b>How to play:</b> Click the available items to add them to the back of your queue. Click DEQUEUE to process the item at the front. The order of dequeued items must match the Target Dequeue Order!</p>" +
            "</body></html>";
        JOptionPane.showMessageDialog(frame, msg, "Tutorial", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetGame() {
        wrongMoves = 0;
        generateChallenge();
        removeAll();
        buildUI();
        revalidate(); repaint();
    }

    private void refresh() {
        removeAll();
        buildUI();
        revalidate(); repaint();
    }

    private JPanel buildLabeledRow(String label, java.util.List<String> items, Color color) {
        JPanel outer = new JPanel(new BorderLayout(0, 4));
        outer.setBackground(BG);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(148, 163, 184));
        outer.add(lbl, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setBackground(CARD_BG);
        row.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 90)));
        for (int i = 0; i < items.size(); i++) {
            row.add(makeTicket(items.get(i), TICKET_COLORS[i % TICKET_COLORS.length], false));
            if (i < items.size() - 1) {
                JLabel arr = new JLabel("->");
                arr.setForeground(new Color(100, 116, 139));
                arr.setFont(new Font("Segoe UI", Font.BOLD, 16));
                row.add(arr);
            }
        }
        outer.add(row, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildLabeledRowPanel(String label, java.util.List<String> items, Color color) {
        JPanel outer = new JPanel(new BorderLayout(0, 4));
        outer.setBackground(BG);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(148, 163, 184));
        outer.add(lbl, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setBackground(CARD_BG);
        row.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 90)));

        if (items.isEmpty()) {
            JLabel empty = new JLabel("(empty)");
            empty.setForeground(new Color(100, 116, 139));
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            row.add(empty);
        }
        for (int i = 0; i < items.size(); i++) {
            final String item = items.get(i);
            final int colorIdx = targetOrder.indexOf(item) % TICKET_COLORS.length;
            boolean clickable = label.contains("Click");
            JButton ticket = makeTicket(item, TICKET_COLORS[Math.max(0, colorIdx)], clickable);
            if (clickable) ticket.addActionListener(e -> doEnqueue(item));
            row.add(ticket);
        }
        outer.add(row, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildQueueDisplayPanel() {
        JPanel outer = new JPanel(new BorderLayout(0, 4));
        outer.setBackground(BG);
        JLabel lbl = new JLabel("Your Queue [FRONT -> BACK]:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(148, 163, 184));
        outer.add(lbl, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setBackground(new Color(25, 40, 60));
        row.setBorder(BorderFactory.createLineBorder(ACCENT));

        java.util.List<String> qList = new ArrayList<>(playerQueue);
        if (qList.isEmpty()) {
            JLabel empty = new JLabel("(empty)");
            empty.setForeground(new Color(100, 116, 139));
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            row.add(empty);
        }
        for (int i = 0; i < qList.size(); i++) {
            int colorIdx = targetOrder.indexOf(qList.get(i)) % TICKET_COLORS.length;
            row.add(makeTicket(qList.get(i), TICKET_COLORS[Math.max(0, colorIdx)], false));
            if (i < qList.size() - 1) {
                JLabel arr = new JLabel("->");
                arr.setForeground(new Color(100, 116, 139));
                arr.setFont(new Font("Segoe UI", Font.BOLD, 16));
                row.add(arr);
            }
        }
        outer.add(row, BorderLayout.CENTER);
        return outer;
    }

    private JButton makeTicket(String text, Color bg, boolean clickable) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        if (clickable) {
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            btn.setEnabled(true);
        }
        return btn;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }
}
