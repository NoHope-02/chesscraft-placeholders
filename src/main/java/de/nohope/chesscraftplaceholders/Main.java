package de.nohope.chesscraftplaceholders;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ChessCraftService chessCraftService;

    @Override
    public void onEnable() {
        this.chessCraftService = new ChessCraftService(this);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().severe("PlaceholderAPI wurde nicht gefunden!");
            return;
        }

        boolean registered = new ChessCraftPlaceholder(this).register();

        if (registered) {
            getLogger().info("ChessCraft Placeholder erfolgreich registriert!");
        } else {
            getLogger().severe("ChessCraft Placeholder konnte NICHT registriert werden!");
        }
    }

    public ChessCraftService getChessCraftService() {
        return chessCraftService;
    }
}