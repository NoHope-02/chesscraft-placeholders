package de.nohope.chesscraftplaceholders;

public class TopPlayerData {
    private final String username;
    private final String displayname;
    private final int elo;
    private final int peakElo;
    private final int ratedMatches;

    public TopPlayerData(String username, String displayname, int elo, int peakElo, int ratedMatches) {
        this.username = username;
        this.displayname = displayname;
        this.elo = elo;
        this.peakElo = peakElo;
        this.ratedMatches = ratedMatches;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public int getElo() {
        return elo;
    }

    public int getPeakElo() {
        return peakElo;
    }

    public int getRatedMatches() {
        return ratedMatches;
    }
}