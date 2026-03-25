package de.nohope.chesscraftplaceholders;

public class EloStatsData {
    private final int totalEloGain;
    private final int totalEloLoss;
    private final int bestEloGain;
    private final int worstEloLoss;
    private final double averageEloChange;

    public EloStatsData(int totalEloGain, int totalEloLoss, int bestEloGain, int worstEloLoss, double averageEloChange) {
        this.totalEloGain = totalEloGain;
        this.totalEloLoss = totalEloLoss;
        this.bestEloGain = bestEloGain;
        this.worstEloLoss = worstEloLoss;
        this.averageEloChange = averageEloChange;
    }

    public int getTotalEloGain() {
        return totalEloGain;
    }

    public int getTotalEloLoss() {
        return totalEloLoss;
    }

    public int getBestEloGain() {
        return bestEloGain;
    }

    public int getWorstEloLoss() {
        return worstEloLoss;
    }

    public double getAverageEloChange() {
        return averageEloChange;
    }
}