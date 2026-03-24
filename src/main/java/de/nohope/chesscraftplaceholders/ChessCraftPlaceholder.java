package de.nohope.chesscraftplaceholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.UUID;

public class ChessCraftPlaceholder extends PlaceholderExpansion {

    private final Main plugin;

    public ChessCraftPlaceholder(Main plugin, ChessCraftService chessCraftService) {
        this.plugin = plugin;
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
        return "1.2.0-SNAPSHOTv2";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        ChessCraftService service = plugin.getChessCraftService();

        switch (identifier.toLowerCase()) {
            case "last_result":
                return service.getLastResult(player.getUniqueId());

            case "last_opponent":
                return service.getLastOpponent(player.getUniqueId());

            case "last_elo_change":
                int change = service.getLastEloChange(player.getUniqueId());
                return change > 0 ? "+" + change : String.valueOf(change);

            case "elo":
                return String.valueOf(service.getElo(player));

            case "rank":
                return String.valueOf(service.getRank(player));

            case "peakelo":
                return String.valueOf(service.getPeakElo(player.getUniqueId()));

            case "rated_matches":
                return String.valueOf(service.getRatedMatches(player.getUniqueId()));

            case "username":
                return service.getUsername(player.getUniqueId());

            case "displayname":
                return service.getDisplayname(player.getUniqueId());
            case "last_side":
                return service.getLastSide(player.getUniqueId());

            case "last_type":
                return service.getLastType(player.getUniqueId());

            case "last_updated":
                return service.getLastUpdated(player.getUniqueId());

            case "last_moves_count":
                return String.valueOf(service.getLastMovesCount(player.getUniqueId()));
            case "last_opponent_displayname":
                return service.getLastOpponentDisplayname(player.getUniqueId());

            case "last_elo_after":
                return String.valueOf(service.getLastEloAfter(player.getUniqueId()));

            case "last_opponent_elo_after":
                return String.valueOf(service.getLastOpponentEloAfter(player.getUniqueId()));

            default:
                if (identifier.toLowerCase().startsWith("top_")) {
                    String[] parts = identifier.toLowerCase().split("_");

                    try {
                        if (parts.length == 2) {
                            int pos = Integer.parseInt(parts[1]);
                            return service.getTop(pos);
                        }

                        if (parts.length == 3) {
                            int pos = Integer.parseInt(parts[1]);
                            String field = parts[2];

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
                                default:
                                    return null;
                            }
                        }

                        if (parts.length == 4) {
                            int pos = Integer.parseInt(parts[1]);
                            String field = parts[2] + "_" + parts[3];

                            TopPlayerData data = service.getTopPlayerData(pos);
                            if (data == null) {
                                return "N/A";
                            }

                            switch (field) {
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
                }
                if (identifier.toLowerCase().startsWith("history_")) {
                    String[] parts = identifier.toLowerCase().split("_");

                    try {
                        if (parts.length == 3) {
                            int pos = Integer.parseInt(parts[1]);
                            String field = parts[2];
                            HistoryData data = service.getHistoryData(player.getUniqueId(), pos);
                            if (data == null) {
                                return "none";
                            }
                            switch (field) {
                                case "result":
                                    return String.valueOf(data.getResult());
                                case "opponent":
                                    return String.valueOf(data.getOpponent());
                                case "side":
                                    return String.valueOf(data.getSide());
                                case "type":
                                    return String.valueOf(data.getType());
                                case "updated":
                                    return String.valueOf(data.getUpdated());

                                    default:
                                        return null;
                            }
                        }
                        if (parts.length == 4) {
                            int pos = Integer.parseInt(parts[1]);
                            String field = parts[2] + "_" + parts[3];
                            HistoryData data = service.getHistoryData(player.getUniqueId(), pos);
                            if (data == null) {
                                return "none";
                            }
                            switch (field) {
                            case "elo_change":
                                change = service.getLastEloChange(player.getUniqueId());
                                return change > 0 ? "+" + change : String.valueOf(data.getEloChange());
                            case "moves_count":
                                return String.valueOf(data.getMovesCount());
                            case "elo_after":
                                return String.valueOf(data.getEloAfter());
                            case "opponent_displayname":
                                return String.valueOf(data.getOpponentDisplayname());
                            default:
                                return null;
                            }
                        }         if (parts.length == 5) {
                            int pos = Integer.parseInt(parts[1]);
                            String field = parts[2] + "_" + parts[3] + "_" + parts[4];
                            HistoryData data = service.getHistoryData(player.getUniqueId(), pos);
                            if (data == null) {
                                return "none";
                            }
                            switch (field) {
                                case "opponent_elo_after":
                                    return String.valueOf(data.getOpponentEloAfter());
                                default:
                                    return null;
                            }
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }

                return null;
        }
    }
}
