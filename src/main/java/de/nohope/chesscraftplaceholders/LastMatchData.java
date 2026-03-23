package de.nohope.chesscraftplaceholders;

public class LastMatchData {
    private final String result;
    private final String opponent;
    private final int eloChange;
    private final String side;
    private final String type;
    private final String updated;
    private final int movesCount;

    public LastMatchData(String result, String opponent, int eloChange, String side, String type, String updated, int movesCount) {
        this.result = result;
        this.opponent = opponent;
        this.eloChange = eloChange;
        this.side = side;
        this.type = type;
        this.updated = updated;
        this.movesCount = movesCount;
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

    public String getSide() {
        return side;
    }

    public String getType() {
        return type;
    }

    public String getUpdated() {
        return updated;
    }

    public int getMovesCount() {
        return movesCount;
    }
}