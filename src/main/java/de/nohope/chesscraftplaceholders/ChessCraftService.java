package de.nohope.chesscraftplaceholders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.UUID;

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

            String sql = "SELECT id FROM chesscraft_players WHERE rated_matches>0 ORDER BY rating DESC, username";
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

            String sql = "SELECT username, rating FROM chesscraft_players WHERE rated_matches>0 ORDER BY rating DESC, username LIMIT 1 OFFSET ?";
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
    public int getPeakElo(UUID uuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT peak_rating FROM chesscraft_players WHERE id = ?"
            );
            stmt.setString(1, uuid.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("peak_rating");
            }
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getRatedMatches(UUID uuid) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT rated_matches FROM chesscraft_players WHERE id = ?"
            );
            stmt.setString(1, uuid.toString());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("rated_matches");
            }
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public LastMatchData getLastMatchData(UUID playerUuid) {
        String sql = """
    SELECT chesscraft_matches.id,
           chesscraft_matches.white_cpu,
           chesscraft_matches.white_player_id,
           chesscraft_matches.black_cpu,
           chesscraft_matches.black_player_id,
           chesscraft_matches.last_updated,
           chesscraft_complete_matches.result_type,
           chesscraft_complete_matches.result_color,
           chesscraft_complete_matches.white_elo_change,
           chesscraft_complete_matches.black_elo_change
    FROM chesscraft_matches
    RIGHT OUTER JOIN chesscraft_complete_matches
        ON chesscraft_matches.id = chesscraft_complete_matches.id
    WHERE white_player_id = ? OR black_player_id = ?
    ORDER BY chesscraft_matches.last_updated DESC
    LIMIT 1
    """;

        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, playerUuid.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    UUID whitePlayerId = rs.getObject("white_player_id", UUID.class);
                    UUID blackPlayerId = rs.getObject("black_player_id", UUID.class);
                    boolean whiteCpu = rs.getBoolean("white_cpu");
                    boolean blackCpu = rs.getBoolean("black_cpu");

                    boolean isWhite = playerUuid.equals(whitePlayerId);

                    String resultColor = rs.getString("result_color");
                    String resultType = rs.getString("result_type");

                    int eloChange = isWhite
                            ? rs.getInt("white_elo_change")
                            : rs.getInt("black_elo_change");

                    String opponent;
                    if (isWhite) {
                        opponent = blackCpu ? "CPU" : getUsername(blackPlayerId);
                    } else {
                        opponent = whiteCpu ? "CPU" : getUsername(whitePlayerId);
                    }

                    String result = parseResult(isWhite, resultColor, resultType, eloChange);

                    return new LastMatchData(result, opponent, eloChange);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    private String parseResult(boolean isWhite, String resultColor, String resultType, int eloChange) {

        // 1. Elo entscheidet (beste Quelle)
        if (eloChange > 0) {
            return "win";
        }

        if (eloChange < 0) {
            return "loss";
        }

        // 2. Fallback für Draws
        if (resultType != null) {
            String lowerType = resultType.toLowerCase();

            if (lowerType.contains("draw")
                    || lowerType.contains("stalemate")
                    || lowerType.contains("repetition")
                    || lowerType.contains("50")) {
                return "draw";
            }
        }

        return "draw";
    }
    public String getLastResult(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getResult() : "none";
    }

    public String getLastOpponent(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getOpponent() : "none";
    }

    public int getLastEloChange(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getEloChange() : 0;
    }
    public String getUsername(UUID uuid) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            String sql = "SELECT username FROM chesscraft_players WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }
    public String getDisplayname(UUID uuid) {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }

            String sql = "SELECT displayname FROM chesscraft_players WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("displayname");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Unknown";
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