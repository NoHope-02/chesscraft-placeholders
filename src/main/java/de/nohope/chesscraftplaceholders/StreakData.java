package de.nohope.chesscraftplaceholders;

public class StreakData {
    private final int currentWinStreak;
    private final int currentLossStreak;
    private final int bestWinStreak;
    private final int bestLossStreak;

    public StreakData(int currentWinStreak, int currentLossStreak, int bestWinStreak, int bestLossStreak) {
        this.currentWinStreak = currentWinStreak;
        this.currentLossStreak = currentLossStreak;
        this.bestWinStreak = bestWinStreak;
        this.bestLossStreak = bestLossStreak;
    }

    public int getCurrentWinStreak() {
        return currentWinStreak;
    }

    public int getCurrentLossStreak() {
        return currentLossStreak;
    }

    public int getBestWinStreak() {
        return bestWinStreak;
    }

    public int getBestLossStreak() {
        return bestLossStreak;
    }
}