package de.nohope.chesscraftplaceholders;

public class LastMatchData {
    private final String result;
    private final String opponent;
    private final int eloChange;
    private final String side;
    private final String type;
    private final String updated;
    private final int movesCount;
    private final String opponentDisplayname;
    private final int eloAfter;
    private final int opponentEloAfter;

    public LastMatchData(String result, String opponent, int eloChange, String side, String type,
                         String updated, int movesCount, String opponentDisplayname,
                         int eloAfter, int opponentEloAfter) {
        this.result = result;
        this.opponent = opponent;
        this.eloChange = eloChange;
        this.side = side;
        this.type = type;
        this.updated = updated;
        this.movesCount = movesCount;
        this.opponentDisplayname = opponentDisplayname;
        this.eloAfter = eloAfter;
        this.opponentEloAfter = opponentEloAfter;
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
    public String getOpponentDisplayname() {
        return opponentDisplayname;
    }

    public int getEloAfter() {
        return eloAfter;
    }

    public int getOpponentEloAfter() {
        return opponentEloAfter;
    }
}