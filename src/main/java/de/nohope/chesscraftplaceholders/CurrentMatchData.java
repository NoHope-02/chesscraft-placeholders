package de.nohope.chesscraftplaceholders;

public class CurrentMatchData {
    private final boolean inMatch;
    private final String opponent;
    private final String side;
    private final String type;
    private final int movesCount;
    private final String fen;

    public CurrentMatchData(boolean inMatch, String opponent, String side, String type, int movesCount, String fen) {
        this.inMatch = inMatch;
        this.opponent = opponent;
        this.side = side;
        this.type = type;
        this.movesCount = movesCount;
        this.fen = fen;
    }

    public boolean isInMatch() {
        return inMatch;
    }

    public String getOpponent() {
        return opponent;
    }

    public String getSide() {
        return side;
    }

    public String getType() {
        return type;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public String getFen() {
        return fen;
    }
}