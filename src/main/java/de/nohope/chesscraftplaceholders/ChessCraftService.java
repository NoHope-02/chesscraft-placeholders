package de.nohope.chesscraftplaceholders;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChessCraftService {

    private static final long PLAYER_CACHE_TTL = 10_000L;
    private static final long TOP_CACHE_TTL = 10_000L;
    private static final long HISTORY_CACHE_TTL = 5_000L;
    private static final long CURRENT_MATCH_CACHE_TTL = 3_000L;

    private final Main plugin;
    private Connection connection;

    private final Map<UUID, CacheEntry<BasicPlayerData>> basicPlayerCache = new ConcurrentHashMap<>();
    private final Map<UUID, CacheEntry<LastMatchData>> lastMatchCache = new ConcurrentHashMap<>();
    private final Map<UUID, CacheEntry<PlayerStatsData>> playerStatsCache = new ConcurrentHashMap<>();
    private final Map<UUID, CacheEntry<StreakData>> streakCache = new ConcurrentHashMap<>();
    private final Map<UUID, CacheEntry<EloStatsData>> eloStatsCache = new ConcurrentHashMap<>();
    private final Map<Integer, CacheEntry<TopPlayerData>> topPlayerCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry<HistoryData>> historyCache = new ConcurrentHashMap<>();
    private final Map<UUID, CacheEntry<CurrentMatchData>> currentMatchCache = new ConcurrentHashMap<>();

    public ChessCraftService(Main plugin) {
        this.plugin = plugin;
        connect();
    }

    private static class CacheEntry<T> {
        private final T value;
        private final long expiresAt;

        private CacheEntry(T value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        private boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }

    private static class SimpleMatchResult {
        private final String result;
        private final String type;
        private final int eloChange;

        public SimpleMatchResult(String result, String type, int eloChange) {
            this.result = result;
            this.type = type;
            this.eloChange = eloChange;
        }

        public String getResult() {
            return result;
        }

        public String getType() {
            return type;
        }

        public int getEloChange() {
            return eloChange;
        }
    }

    private static class BasicPlayerData {
        private final int elo;
        private final int peakElo;
        private final int ratedMatches;
        private final String username;
        private final String displayname;

        private BasicPlayerData(int elo, int peakElo, int ratedMatches, String username, String displayname) {
            this.elo = elo;
            this.peakElo = peakElo;
            this.ratedMatches = ratedMatches;
            this.username = username;
            this.displayname = displayname;
        }

        public int getElo() {
            return elo;
        }

        public int getPeakElo() {
            return peakElo;
        }

        public int getRatedMatches() {
            return ratedMatches;
        }

        public String getUsername() {
            return username;
        }

        public String getDisplayname() {
            return displayname;
        }
    }

    private void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
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
            String url = "jdbc:h2:" + databaseFile.getAbsolutePath() + ";MODE=MySQL";

            connection = DriverManager.getConnection(url, "", "");
            plugin.getLogger().info("Verbindung zur ChessCraft-Datenbank hergestellt: " + url);

        } catch (Exception e) {
            plugin.getLogger().severe("Fehler beim Verbinden mit der ChessCraft-Datenbank:");
            e.printStackTrace();
        }
    }

    private <T> T getCached(Map<UUID, CacheEntry<T>> cache, UUID key) {
        CacheEntry<T> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    private <T> void putCached(Map<UUID, CacheEntry<T>> cache, UUID key, T value, long ttl) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttl));
    }

    private <T> T getCachedIntKey(Map<Integer, CacheEntry<T>> cache, int key) {
        CacheEntry<T> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    private <T> void putCachedIntKey(Map<Integer, CacheEntry<T>> cache, int key, T value, long ttl) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttl));
    }

    private <T> T getCachedStringKey(Map<String, CacheEntry<T>> cache, String key) {
        CacheEntry<T> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    private <T> void putCachedStringKey(Map<String, CacheEntry<T>> cache, String key, T value, long ttl) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttl));
    }

    private BasicPlayerData loadBasicPlayerData(UUID uuid) {
        try {
            ensureConnection();

            String sql = """
                    SELECT rating, peak_rating, rated_matches, username, displayname
                    FROM chesscraft_players
                    WHERE id = ?
                    """;

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, uuid.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new BasicPlayerData(
                                rs.getInt("rating"),
                                rs.getInt("peak_rating"),
                                rs.getInt("rated_matches"),
                                rs.getString("username"),
                                safeDisplayname(rs.getString("displayname"))
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new BasicPlayerData(0, 0, 0, "Unknown", "Unknown");
    }

    private BasicPlayerData getBasicPlayerData(UUID uuid) {
        BasicPlayerData cached = getCached(basicPlayerCache, uuid);
        if (cached != null) {
            return cached;
        }

        BasicPlayerData loaded = loadBasicPlayerData(uuid);
        putCached(basicPlayerCache, uuid, loaded, PLAYER_CACHE_TTL);
        return loaded;
    }

    public int getElo(Player player) {
        return getBasicPlayerData(player.getUniqueId()).getElo();
    }

    public int getRank(Player player) {
        try {
            ensureConnection();

            String sql = "SELECT id FROM chesscraft_players WHERE rated_matches > 0 ORDER BY rating DESC, username";
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
        TopPlayerData data = getTopPlayerData(position);
        if (data != null) {
            return data.getUsername() + " (" + data.getElo() + ")";
        }
        return "N/A";
    }

    public TopPlayerData getTopPlayerData(int position) {
        TopPlayerData cached = getCachedIntKey(topPlayerCache, position);
        if (cached != null) {
            return cached;
        }

        try {
            ensureConnection();

            String sql = """
                    SELECT username, displayname, rating, peak_rating, rated_matches
                    FROM chesscraft_players
                    WHERE rated_matches > 0
                    ORDER BY rating DESC, username
                    LIMIT 1 OFFSET ?
                    """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, position - 1);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        TopPlayerData loaded = new TopPlayerData(
                                rs.getString("username"),
                                safeDisplayname(rs.getString("displayname")),
                                rs.getInt("rating"),
                                rs.getInt("peak_rating"),
                                rs.getInt("rated_matches")
                        );
                        putCachedIntKey(topPlayerCache, position, loaded, TOP_CACHE_TTL);
                        return loaded;
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Fehler bei getTopPlayerData(" + position + ")");
            e.printStackTrace();
        }

        return null;
    }

    public int getPeakElo(UUID uuid) {
        return getBasicPlayerData(uuid).getPeakElo();
    }

    public int getRatedMatches(UUID uuid) {
        return getBasicPlayerData(uuid).getRatedMatches();
    }

    public String getUsername(UUID uuid) {
        return getBasicPlayerData(uuid).getUsername();
    }

    public String getDisplayname(UUID uuid) {
        return getBasicPlayerData(uuid).getDisplayname();
    }

    private String safeDisplayname(String raw) {
        if (raw == null || raw.isBlank()) {
            return "Unknown";
        }

        if (raw.startsWith("{")) {
            return extractPlainText(raw);
        }

        return raw;
    }

    private String extractPlainText(String json) {
        StringBuilder result = new StringBuilder();

        try {
            String[] parts = json.split("\"text\":\"");

            for (int i = 1; i < parts.length; i++) {
                String textPart = parts[i].split("\"")[0];
                result.append(textPart);
            }
        } catch (Exception ignored) {
            return json;
        }

        String finalText = result.toString().trim();
        return finalText.isEmpty() ? "Unknown" : finalText;
    }

    private String parseResult(String resultType, String resultColor, int eloChange, boolean isWhite) {
        if (eloChange > 0) {
            return "win";
        }

        if (eloChange < 0) {
            return "loss";
        }

        if (resultType != null) {
            String lowerType = resultType.toLowerCase();

            if (lowerType.contains("draw")
                    || lowerType.contains("stalemate")
                    || lowerType.contains("repetition")
                    || lowerType.contains("50")) {
                return "draw";
            }

            if (lowerType.contains("forfeit")) {
                return "loss";
            }
        }

        return "draw";
    }

    private LastMatchData loadLastMatchData(UUID playerUuid) {
        String sql = """
                SELECT chesscraft_matches.id,
                       chesscraft_matches.white_cpu,
                       chesscraft_matches.white_player_id,
                       chesscraft_matches.black_cpu,
                       chesscraft_matches.black_player_id,
                       chesscraft_matches.last_updated,
                       chesscraft_matches.moves,
                       chesscraft_complete_matches.white_elo,
                       chesscraft_complete_matches.black_elo,
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
            ensureConnection();

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
                    String opponentDisplayname;

                    if (isWhite) {
                        if (blackCpu) {
                            opponent = "CPU";
                            opponentDisplayname = "CPU";
                        } else {
                            opponent = getUsername(blackPlayerId);
                            opponentDisplayname = getDisplayname(blackPlayerId);
                        }
                    } else {
                        if (whiteCpu) {
                            opponent = "CPU";
                            opponentDisplayname = "CPU";
                        } else {
                            opponent = getUsername(whitePlayerId);
                            opponentDisplayname = getDisplayname(whitePlayerId);
                        }
                    }

                    String side = isWhite ? "white" : "black";
                    String type = (whiteCpu || blackCpu) ? "cpu" : "pvp";

                    Timestamp timestamp = rs.getTimestamp("last_updated");
                    String updated = timestamp != null ? timestamp.toString() : "none";

                    String moves = rs.getString("moves");
                    int movesCount = (moves == null || moves.isBlank()) ? 0 : moves.split(",").length;

                    String result = parseResult(resultType, resultColor, eloChange, isWhite);

                    int whiteElo = rs.getInt("white_elo");
                    int blackElo = rs.getInt("black_elo");
                    int whiteEloChange = rs.getInt("white_elo_change");
                    int blackEloChange = rs.getInt("black_elo_change");

                    int eloAfter = isWhite ? whiteElo + whiteEloChange : blackElo + blackEloChange;
                    int opponentEloAfter = isWhite ? blackElo + blackEloChange : whiteElo + whiteEloChange;

                    return new LastMatchData(
                            result,
                            opponent,
                            eloChange,
                            side,
                            type,
                            updated,
                            movesCount,
                            opponentDisplayname,
                            eloAfter,
                            opponentEloAfter
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public LastMatchData getLastMatchData(UUID playerUuid) {
        LastMatchData cached = getCached(lastMatchCache, playerUuid);
        if (cached != null) {
            return cached;
        }

        LastMatchData loaded = loadLastMatchData(playerUuid);
        if (loaded != null) {
            putCached(lastMatchCache, playerUuid, loaded, PLAYER_CACHE_TTL);
        }
        return loaded;
    }

    public HistoryData getHistoryData(UUID playerUuid, int index) {
        String cacheKey = playerUuid + ":" + index;
        HistoryData cached = getCachedStringKey(historyCache, cacheKey);
        if (cached != null) {
            return cached;
        }

        String sql = """
                SELECT chesscraft_complete_matches.white_elo,
                       chesscraft_complete_matches.black_elo,
                       chesscraft_complete_matches.result_type,
                       chesscraft_complete_matches.result_color,
                       chesscraft_complete_matches.white_elo_change,
                       chesscraft_complete_matches.black_elo_change,
                       chesscraft_matches.white_cpu,
                       chesscraft_matches.white_player_id,
                       chesscraft_matches.black_cpu,
                       chesscraft_matches.black_player_id,
                       chesscraft_matches.last_updated,
                       chesscraft_matches.moves
                FROM chesscraft_matches
                RIGHT OUTER JOIN chesscraft_complete_matches
                    ON chesscraft_matches.id = chesscraft_complete_matches.id
                WHERE white_player_id = ? OR black_player_id = ?
                ORDER BY chesscraft_matches.last_updated DESC
                LIMIT 1 OFFSET ?
                """;

        try {
            ensureConnection();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, playerUuid.toString());
                stmt.setInt(3, index - 1);

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
                    String opponentDisplayname;

                    if (isWhite) {
                        if (blackCpu) {
                            opponent = "CPU";
                            opponentDisplayname = "CPU";
                        } else {
                            opponent = getUsername(blackPlayerId);
                            opponentDisplayname = getDisplayname(blackPlayerId);
                        }
                    } else {
                        if (whiteCpu) {
                            opponent = "CPU";
                            opponentDisplayname = "CPU";
                        } else {
                            opponent = getUsername(whitePlayerId);
                            opponentDisplayname = getDisplayname(whitePlayerId);
                        }
                    }

                    String side = isWhite ? "white" : "black";
                    String type = (whiteCpu || blackCpu) ? "cpu" : "pvp";

                    Timestamp timestamp = rs.getTimestamp("last_updated");
                    String updated = timestamp != null ? timestamp.toString() : "none";

                    String moves = rs.getString("moves");
                    int movesCount = (moves == null || moves.isBlank()) ? 0 : moves.split(",").length;

                    String result = parseResult(resultType, resultColor, eloChange, isWhite);

                    int whiteElo = rs.getInt("white_elo");
                    int blackElo = rs.getInt("black_elo");
                    int whiteEloChange = rs.getInt("white_elo_change");
                    int blackEloChange = rs.getInt("black_elo_change");

                    int eloAfter = isWhite ? whiteElo + whiteEloChange : blackElo + blackEloChange;
                    int opponentEloAfter = isWhite ? blackElo + blackEloChange : whiteElo + whiteEloChange;

                    HistoryData loaded = new HistoryData(
                            result,
                            opponent,
                            eloChange,
                            side,
                            type,
                            updated,
                            movesCount,
                            opponentDisplayname,
                            eloAfter,
                            opponentEloAfter
                    );

                    putCachedStringKey(historyCache, cacheKey, loaded, HISTORY_CACHE_TTL);
                    return loaded;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<SimpleMatchResult> getCompletedMatchResults(UUID playerUuid) {
        String sql = """
                SELECT chesscraft_matches.white_player_id,
                       chesscraft_matches.black_player_id,
                       chesscraft_matches.white_cpu,
                       chesscraft_matches.black_cpu,
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
                """;

        List<SimpleMatchResult> results = new ArrayList<>();

        try {
            ensureConnection();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, playerUuid.toString());
                stmt.setString(2, playerUuid.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        UUID whitePlayerId = rs.getObject("white_player_id", UUID.class);
                        boolean whiteCpu = rs.getBoolean("white_cpu");
                        boolean blackCpu = rs.getBoolean("black_cpu");
                        boolean isWhite = playerUuid.equals(whitePlayerId);

                        int eloChange = isWhite
                                ? rs.getInt("white_elo_change")
                                : rs.getInt("black_elo_change");

                        String resultType = rs.getString("result_type");
                        String resultColor = rs.getString("result_color");

                        String result = parseResult(resultType, resultColor, eloChange, isWhite);
                        String type = (whiteCpu || blackCpu) ? "cpu" : "pvp";

                        results.add(new SimpleMatchResult(result, type, eloChange));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    private PlayerStatsData loadPlayerStatsData(UUID playerUuid) {
        List<SimpleMatchResult> matches = getCompletedMatchResults(playerUuid);

        int wins = 0;
        int losses = 0;
        int draws = 0;
        int pvpWins = 0;
        int cpuWins = 0;

        for (SimpleMatchResult match : matches) {
            if ("win".equals(match.getResult())) {
                wins++;
                if ("pvp".equals(match.getType())) {
                    pvpWins++;
                } else {
                    cpuWins++;
                }
            } else if ("loss".equals(match.getResult())) {
                losses++;
            } else if ("draw".equals(match.getResult())) {
                draws++;
            }
        }

        return new PlayerStatsData(wins, losses, draws, pvpWins, cpuWins);
    }

    public PlayerStatsData getPlayerStatsData(UUID playerUuid) {
        PlayerStatsData cached = getCached(playerStatsCache, playerUuid);
        if (cached != null) {
            return cached;
        }

        PlayerStatsData loaded = loadPlayerStatsData(playerUuid);
        putCached(playerStatsCache, playerUuid, loaded, PLAYER_CACHE_TTL);
        return loaded;
    }

    public int getWins(UUID uuid) {
        return getPlayerStatsData(uuid).getWins();
    }

    public int getLosses(UUID uuid) {
        return getPlayerStatsData(uuid).getLosses();
    }

    public int getDraws(UUID uuid) {
        return getPlayerStatsData(uuid).getDraws();
    }

    public int getPvpWins(UUID uuid) {
        return getPlayerStatsData(uuid).getPvpWins();
    }

    public int getCpuWins(UUID uuid) {
        return getPlayerStatsData(uuid).getCpuWins();
    }

    public String getWinrate(UUID uuid) {
        return String.format(Locale.US, "%.2f", getPlayerStatsData(uuid).getWinrate());
    }

    private StreakData loadStreakData(UUID playerUuid) {
        List<SimpleMatchResult> matches = getCompletedMatchResults(playerUuid);

        int bestWinStreak = 0;
        int bestLossStreak = 0;
        int runningWin = 0;
        int runningLoss = 0;

        for (SimpleMatchResult match : matches) {
            if ("win".equals(match.getResult())) {
                runningWin++;
                runningLoss = 0;
                if (runningWin > bestWinStreak) {
                    bestWinStreak = runningWin;
                }
            } else if ("loss".equals(match.getResult())) {
                runningLoss++;
                runningWin = 0;
                if (runningLoss > bestLossStreak) {
                    bestLossStreak = runningLoss;
                }
            } else {
                runningWin = 0;
                runningLoss = 0;
            }
        }

        int currentWinStreak = 0;
        int currentLossStreak = 0;

        if (!matches.isEmpty()) {
            String firstResult = matches.get(0).getResult();

            if ("win".equals(firstResult)) {
                for (SimpleMatchResult match : matches) {
                    if ("win".equals(match.getResult())) {
                        currentWinStreak++;
                    } else {
                        break;
                    }
                }
            } else if ("loss".equals(firstResult)) {
                for (SimpleMatchResult match : matches) {
                    if ("loss".equals(match.getResult())) {
                        currentLossStreak++;
                    } else {
                        break;
                    }
                }
            }
        }

        return new StreakData(currentWinStreak, currentLossStreak, bestWinStreak, bestLossStreak);
    }

    public StreakData getStreakData(UUID playerUuid) {
        StreakData cached = getCached(streakCache, playerUuid);
        if (cached != null) {
            return cached;
        }

        StreakData loaded = loadStreakData(playerUuid);
        putCached(streakCache, playerUuid, loaded, PLAYER_CACHE_TTL);
        return loaded;
    }

    public int getCurrentWinStreak(UUID uuid) {
        return getStreakData(uuid).getCurrentWinStreak();
    }

    public int getCurrentLossStreak(UUID uuid) {
        return getStreakData(uuid).getCurrentLossStreak();
    }

    public int getBestWinStreak(UUID uuid) {
        return getStreakData(uuid).getBestWinStreak();
    }

    public int getBestLossStreak(UUID uuid) {
        return getStreakData(uuid).getBestLossStreak();
    }

    private EloStatsData loadEloStatsData(UUID playerUuid) {
        List<SimpleMatchResult> matches = getCompletedMatchResults(playerUuid);

        int totalGain = 0;
        int totalLoss = 0;
        int bestGain = 0;
        int worstLoss = 0;
        int sum = 0;
        int count = 0;

        for (SimpleMatchResult match : matches) {
            int change = match.getEloChange();
            sum += change;
            count++;

            if (change > 0) {
                totalGain += change;
                if (change > bestGain) {
                    bestGain = change;
                }
            } else if (change < 0) {
                totalLoss += Math.abs(change);
                if (change < worstLoss) {
                    worstLoss = change;
                }
            }
        }

        double average = count == 0 ? 0.0 : (sum * 1.0) / count;
        return new EloStatsData(totalGain, totalLoss, bestGain, worstLoss, average);
    }

    public EloStatsData getEloStatsData(UUID playerUuid) {
        EloStatsData cached = getCached(eloStatsCache, playerUuid);
        if (cached != null) {
            return cached;
        }

        EloStatsData loaded = loadEloStatsData(playerUuid);
        putCached(eloStatsCache, playerUuid, loaded, PLAYER_CACHE_TTL);
        return loaded;
    }

    public int getTotalEloGain(UUID uuid) {
        return getEloStatsData(uuid).getTotalEloGain();
    }

    public int getTotalEloLoss(UUID uuid) {
        return getEloStatsData(uuid).getTotalEloLoss();
    }

    public int getBestEloGain(UUID uuid) {
        return getEloStatsData(uuid).getBestEloGain();
    }

    public int getWorstEloLoss(UUID uuid) {
        return getEloStatsData(uuid).getWorstEloLoss();
    }

    public String getAverageEloChange(UUID uuid) {
        return String.format(Locale.US, "%.2f", getEloStatsData(uuid).getAverageEloChange());
    }

    private CurrentMatchData loadCurrentMatchData(UUID playerUuid) {
        String sql = """
                SELECT chesscraft_matches.id,
                       chesscraft_matches.white_cpu,
                       chesscraft_matches.white_player_id,
                       chesscraft_matches.black_cpu,
                       chesscraft_matches.black_player_id,
                       chesscraft_matches.moves,
                       chesscraft_matches.current_fen
                FROM chesscraft_matches
                LEFT OUTER JOIN chesscraft_complete_matches
                    ON chesscraft_matches.id = chesscraft_complete_matches.id
                WHERE (white_player_id = ? OR black_player_id = ?)
                  AND chesscraft_complete_matches.result_type IS NULL
                ORDER BY chesscraft_matches.last_updated DESC
                LIMIT 1
                """;

        try {
            ensureConnection();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, playerUuid);
                stmt.setObject(2, playerUuid);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return new CurrentMatchData(false, "none", "none", "none", 0, "none");
                    }

                    UUID whitePlayerId = rs.getObject("white_player_id", UUID.class);
                    UUID blackPlayerId = rs.getObject("black_player_id", UUID.class);
                    boolean whiteCpu = rs.getBoolean("white_cpu");
                    boolean blackCpu = rs.getBoolean("black_cpu");
                    boolean isWhite = playerUuid.equals(whitePlayerId);

                    String opponent;
                    if (isWhite) {
                        opponent = blackCpu ? "CPU" : getUsername(blackPlayerId);
                    } else {
                        opponent = whiteCpu ? "CPU" : getUsername(whitePlayerId);
                    }

                    String side = isWhite ? "white" : "black";
                    String type = (whiteCpu || blackCpu) ? "cpu" : "pvp";

                    String moves = rs.getString("moves");
                    int movesCount = (moves == null || moves.isBlank()) ? 0 : moves.split(",").length;

                    String fen = rs.getString("current_fen");
                    if (fen == null || fen.isBlank()) {
                        fen = "none";
                    }

                    return new CurrentMatchData(true, opponent, side, type, movesCount, fen);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new CurrentMatchData(false, "none", "none", "none", 0, "none");
    }

    public CurrentMatchData getCurrentMatchData(UUID playerUuid) {
        CurrentMatchData cached = getCached(currentMatchCache, playerUuid);
        if (cached != null) {
            return cached;
        }

        CurrentMatchData loaded = loadCurrentMatchData(playerUuid);
        putCached(currentMatchCache, playerUuid, loaded, CURRENT_MATCH_CACHE_TTL);
        return loaded;
    }

    public String getCurrentOpponent(UUID uuid) {
        return getCurrentMatchData(uuid).getOpponent();
    }

    public String getCurrentSide(UUID uuid) {
        return getCurrentMatchData(uuid).getSide();
    }

    public String getCurrentType(UUID uuid) {
        return getCurrentMatchData(uuid).getType();
    }

    public int getCurrentMovesCount(UUID uuid) {
        return getCurrentMatchData(uuid).getMovesCount();
    }

    public String getCurrentFen(UUID uuid) {
        return getCurrentMatchData(uuid).getFen();
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

    public String getLastSide(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getSide() : "none";
    }

    public String getLastType(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getType() : "none";
    }

    public String getLastUpdated(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getUpdated() : "none";
    }

    public int getLastMovesCount(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getMovesCount() : 0;
    }

    public String getLastOpponentDisplayname(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getOpponentDisplayname() : "none";
    }

    public int getLastEloAfter(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getEloAfter() : 0;
    }

    public int getLastOpponentEloAfter(UUID uuid) {
        LastMatchData data = getLastMatchData(uuid);
        return data != null ? data.getOpponentEloAfter() : 0;
    }

    public void clearCaches() {
        basicPlayerCache.clear();
        lastMatchCache.clear();
        playerStatsCache.clear();
        streakCache.clear();
        eloStatsCache.clear();
        topPlayerCache.clear();
        historyCache.clear();
        currentMatchCache.clear();
    }

    public void close() {
        clearCaches();

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}