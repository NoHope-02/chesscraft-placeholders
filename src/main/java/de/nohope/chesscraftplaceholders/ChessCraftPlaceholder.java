package de.nohope.chesscraftplaceholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChessCraftPlaceholder extends PlaceholderExpansion {

    private final Main plugin;

    public ChessCraftPlaceholder(Main plugin) {
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
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        ChessCraftService service = plugin.getChessCraftService();

        switch (identifier.toLowerCase()) {
            case "elo":
                return String.valueOf(service.getElo(player));
            case "rank":
                return String.valueOf(service.getRank(player));
            default:
                if (identifier.toLowerCase().startsWith("top_")) {
                    try {
                        int pos = Integer.parseInt(identifier.substring(4));
                        return service.getTop(pos);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
                return null;
        }
    }
}