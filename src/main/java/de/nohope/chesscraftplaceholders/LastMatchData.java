package de.nohope.chesscraftplaceholders;

public class LastMatchData {
    private final String result;
    private final String opponent;
    private final int eloChange;

    public LastMatchData(String result, String opponent, int eloChange) {
        this.result = result;
        this.opponent = opponent;
        this.eloChange = eloChange;
    }

    public String getResult() {
        return result;
    }

    public String getOpponent() {
        return opponent;
    }

    public int getEloChange() {
        return eloChange;
    }
}