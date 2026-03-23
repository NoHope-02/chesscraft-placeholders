package de.nohope.chesscraftplaceholders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;

public class ChessCraftService {

    private final Main plugin;
    private Connection connection;

    public ChessCraftService(Main plugin) {
        this.plugin = plugin;
        connect();
    }

    private void connect() {
        try {
            Plugin chessCraft = Bukkit.getPluginManager().getPlugin("ChessCraft");

            if (chessCraft == null) {
                plugin.getLogger().severe("ChessCraft wurde nicht gefunden!");
                return;
            }

            File dataFolder = chessCraft.getDataFolder();
            File databaseFile = new File(dataFolder, "database");

            String url = "jdbc:h2:" + databaseFile.getAbsolutePath();

            connection = DriverManager.getConnection(url, "", "");
            plugin.getLogger().info("Verbindung zur ChessCraft-Datenbank hergestellt: " + url);

        } catch (Exception e) {
            plugin.getLogger().severe("Fehler beim Verbinden mit der ChessCraft-Datenbank:");
            e.printStackTrace();
        }
    }

    public int getElo(Player player) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            String sql = "SELECT rating FROM chesscraft_players WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setObject(1, player.getUniqueId());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("rating");
                    }
                }
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Fehler bei getElo() für " + player.getName());
            e.printStackTrace();
        }

        return 0;
    }

    public int getRank(Player player) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            String sql = "SELECT id FROM chesscraft_players ORDER BY rating DESC";
            try (PreparedStatement ps = connection.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                int rank = 1;

                while (rs.next()) {
                    Object value = rs.getObject("id");

                    if (value != null && value.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
                        return rank;
                    }

                    rank++;
                }
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Fehler bei getRank() für " + player.getName());
            e.printStackTrace();
        }

        return 0;
    }

    public String getTop(int position) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            String sql = "SELECT username, rating FROM chesscraft_players ORDER BY rating DESC LIMIT 1 OFFSET ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, position - 1);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        int rating = rs.getInt("rating");
                        return username + " (" + rating + ")";
                    }
                }
            }

        } catch (Exception e) {
            plugin.getLogger().severe("Fehler bei getTop(" + position + ")");
            e.printStackTrace();
        }

        return "N/A";
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}