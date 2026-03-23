package de.nohope.chesscraftplaceholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return "1.2.0";
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

                return null;
        }
    }
}
