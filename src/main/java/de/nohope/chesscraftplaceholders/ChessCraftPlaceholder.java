package de.nohope.chesscraftplaceholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChessCraftPlaceholder extends PlaceholderExpansion {

    private final Main plugin;
    private final ChessCraftService service;

    public ChessCraftPlaceholder(Main plugin, ChessCraftService chessCraftService) {
        this.plugin = plugin;
        this.service = chessCraftService;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "chesscraft";
    }

    @Override
    public @NotNull String getAuthor() {
        return "nohope";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.4.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        String id = identifier.toLowerCase();

        switch (id) {
            case "elo":
                return String.valueOf(service.getElo(player));

            case "rank":
                return String.valueOf(service.getRank(player));

            case "peak_elo":
            case "peakelo":
                return String.valueOf(service.getPeakElo(player.getUniqueId()));

            case "rated_matches":
                return String.valueOf(service.getRatedMatches(player.getUniqueId()));

            case "username":
                return service.getUsername(player.getUniqueId());

            case "displayname":
                return service.getDisplayname(player.getUniqueId());

            case "last_result":
                return service.getLastResult(player.getUniqueId());

            case "last_opponent":
                return service.getLastOpponent(player.getUniqueId());

            case "last_opponent_displayname":
                return service.getLastOpponentDisplayname(player.getUniqueId());

            case "last_elo_change":
                int lastChange = service.getLastEloChange(player.getUniqueId());
                return lastChange > 0 ? "+" + lastChange : String.valueOf(lastChange);

            case "last_elo_after":
                return String.valueOf(service.getLastEloAfter(player.getUniqueId()));

            case "last_opponent_elo_after":
                return String.valueOf(service.getLastOpponentEloAfter(player.getUniqueId()));

            case "last_side":
                return service.getLastSide(player.getUniqueId());

            case "last_type":
                return service.getLastType(player.getUniqueId());

            case "last_updated":
                return service.getLastUpdated(player.getUniqueId());

            case "last_moves_count":
                return String.valueOf(service.getLastMovesCount(player.getUniqueId()));

            case "wins":
                return String.valueOf(service.getWins(player.getUniqueId()));

            case "losses":
                return String.valueOf(service.getLosses(player.getUniqueId()));

            case "draws":
                return String.valueOf(service.getDraws(player.getUniqueId()));

            case "winrate":
                return service.getWinrate(player.getUniqueId());

            case "pvp_wins":
                return String.valueOf(service.getPvpWins(player.getUniqueId()));

            case "cpu_wins":
                return String.valueOf(service.getCpuWins(player.getUniqueId()));

            case "current_win_streak":
                return String.valueOf(service.getCurrentWinStreak(player.getUniqueId()));

            case "current_loss_streak":
                return String.valueOf(service.getCurrentLossStreak(player.getUniqueId()));

            case "best_win_streak":
                return String.valueOf(service.getBestWinStreak(player.getUniqueId()));

            case "best_loss_streak":
                return String.valueOf(service.getBestLossStreak(player.getUniqueId()));

            case "total_elo_gain":
                return String.valueOf(service.getTotalEloGain(player.getUniqueId()));

            case "total_elo_loss":
                return String.valueOf(service.getTotalEloLoss(player.getUniqueId()));

            case "best_elo_gain":
                return String.valueOf(service.getBestEloGain(player.getUniqueId()));

            case "worst_elo_loss":
                return String.valueOf(service.getWorstEloLoss(player.getUniqueId()));

            case "average_elo_change":
                return service.getAverageEloChange(player.getUniqueId());

            case "current_opponent":
                return service.getCurrentOpponent(player.getUniqueId());

            case "current_side":
                return service.getCurrentSide(player.getUniqueId());

            case "current_type":
                return service.getCurrentType(player.getUniqueId());

            case "current_moves_count":
                return String.valueOf(service.getCurrentMovesCount(player.getUniqueId()));

            case "current_fen":
                return service.getCurrentFen(player.getUniqueId());
        }

        if (id.startsWith("top_")) {
            return handleTopPlaceholder(id);
        }

        if (id.startsWith("history_")) {
            return handleHistoryPlaceholder(player, id);
        }

        return null;
    }

    private String handleTopPlaceholder(String id) {
        String[] parts = id.split("_");

        try {
            if (parts.length == 2) {
                int pos = Integer.parseInt(parts[1]);
                return service.getTop(pos);
            }

            if (parts.length >= 3) {
                int pos = Integer.parseInt(parts[1]);
                String field = id.substring(("top_" + parts[1] + "_").length());

                TopPlayerData data = service.getTopPlayerData(pos);
                if (data == null) {
                    return "N/A";
                }

                switch (field) {
                    case "name":
                        return data.getUsername();
                    case "displayname":
                        return data.getDisplayname();
                    case "elo":
                        return String.valueOf(data.getElo());
                    case "peak_elo":
                        return String.valueOf(data.getPeakElo());
                    case "rated_matches":
                        return String.valueOf(data.getRatedMatches());
                    default:
                        return null;
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    private String handleHistoryPlaceholder(Player player, String id) {
        String[] parts = id.split("_");
        if (parts.length < 3) {
            return null;
        }

        try {
            int pos = Integer.parseInt(parts[1]);
            String field = id.substring(("history_" + parts[1] + "_").length());

            HistoryData data = service.getHistoryData(player.getUniqueId(), pos);
            if (data == null) {
                return "none";
            }

            switch (field) {
                case "result":
                    return data.getResult();

                case "opponent":
                    return data.getOpponent();

                case "opponent_displayname":
                    return data.getOpponentDisplayname();

                case "elo_change":
                    int historyChange = data.getEloChange();
                    return historyChange > 0 ? "+" + historyChange : String.valueOf(historyChange);

                case "elo_after":
                    return String.valueOf(data.getEloAfter());

                case "opponent_elo_after":
                    return String.valueOf(data.getOpponentEloAfter());

                case "side":
                    return data.getSide();

                case "type":
                    return data.getType();

                case "updated":
                    return data.getUpdated();

                case "moves_count":
                    return String.valueOf(data.getMovesCount());

                case "summary":
                    String resultShort;
                    switch (data.getResult()) {
                        case "win":
                            resultShort = "§aW";
                            break;
                        case "loss":
                            resultShort = "§cL";
                            break;
                        default:
                            resultShort = "§7D";
                            break;
                    }

                    String opponent = data.getOpponentDisplayname();
                    if (opponent == null || opponent.isBlank() || opponent.equalsIgnoreCase("Unknown")) {
                        opponent = data.getOpponent();
                    }

                    int summaryChange = data.getEloChange();
                    String eloChange = summaryChange > 0 ? "+" + summaryChange : String.valueOf(summaryChange);

                    return resultShort + " vs " + opponent + " (" + eloChange + ")";

                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }
}