package de.nohope.chesscraftplaceholders;

public class PlayerStatsData {

    private final int wins;
    private final int losses;
    private final int draws;
    private final int pvpWins;
    private final int cpuWins;

    public PlayerStatsData(int wins, int losses, int draws, int pvpWins, int cpuWins) {
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.pvpWins = pvpWins;
        this.cpuWins = cpuWins;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }

    public int getPvpWins() {
        return pvpWins;
    }

    public int getCpuWins() {
        return cpuWins;
    }

    public int getTotalMatches() {
        return wins + losses + draws;
    }

    public double getWinrate() {
        int total = getTotalMatches();
        if (total == 0) {
            return 0.0;
        }
        return (wins * 100.0) / total;
    }
}