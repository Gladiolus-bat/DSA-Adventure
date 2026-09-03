package com.dsagame;

/**
 * Holds global game state: score, level, lives, and which games have been completed.
 */
public class GameState {
    private int totalScore = 0;
    private int lives = 3;
    private boolean[] completed = new boolean[4]; // 0=BubbleSort, 1=Stack, 2=Queue, 3=BinarySearch

    public int getTotalScore()          { return totalScore; }
    public void addScore(int pts)       { totalScore += pts; }
    public int getLives()               { return lives; }
    public void loseLife()              { if (lives > 0) lives--; }
    public void gainLife()              { lives++; }
    public boolean isCompleted(int idx) { return completed[idx]; }
    public void setCompleted(int idx)   { completed[idx] = true; }
    public int completedCount() {
        int c = 0;
        for (boolean b : completed) if (b) c++;
        return c;
    }
    public void reset() {
        totalScore = 0;
        lives = 3;
        completed = new boolean[4];
    }
}
