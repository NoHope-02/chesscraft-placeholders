package de.nohope.chesscraftplaceholders;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ChessCraftService chessCraftService;

    @Override
    public void onEnable() {
        try {
            chessCraftService = new ChessCraftService(this);

            if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new ChessCraftPlaceholder(this, chessCraftService).register();
            } else {
                getLogger().warning("PlaceholderAPI nicht gefunden, Placeholder werden nicht registriert.");
            }

            getLogger().info("ChessCraftPlaceholderAddon aktiviert.");
        } catch (Exception e) {
            getLogger().severe("Fehler beim Starten des Plugins: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    public ChessCraftService getChessCraftService() {
        return chessCraftService;
    }

    @Override
    public void onDisable() {
        if (chessCraftService != null) {
            chessCraftService.close();
        }
    }
}