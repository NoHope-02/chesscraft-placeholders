# ♟️ ChessCraft Placeholders

A **PlaceholderAPI addon** for [ChessCraft](https://github.com/jpenilla/chesscraft)  
providing player stats, leaderboard data and match information.

---

## ✨ Features

- 📊 Player stats (Elo, peak Elo, rank, matches)
- 🏆 Advanced leaderboard placeholders
- ♟️ Detailed last match information
- 🧠 Clean and lightweight implementation
- ⚡ Works directly with the ChessCraft database

---

## 📦 Placeholders

### 👤 Player

| Placeholder | Description |
|------------|------------|
| `%chesscraft_username%` | Player username |
| `%chesscraft_displayname%` | Player display name |
| `%chesscraft_elo%` | Current Elo |
| `%chesscraft_peak_elo%` | Highest reached Elo |
| `%chesscraft_rank%` | Leaderboard rank |
| `%chesscraft_rated_matches%` | Number of rated matches |

---

### 🏆 Leaderboard

| Placeholder | Description |
|------------|------------|
| `%chesscraft_top_1%` → `%chesscraft_top_10%` | Formatted top players |
| `%chesscraft_top_1_name%` | Username of player |
| `%chesscraft_top_1_displayname%` | Display name |
| `%chesscraft_top_1_elo%` | Current Elo |
| `%chesscraft_top_1_peak_elo%` | Peak Elo |
| `%chesscraft_top_1_rated_matches%` | Rated matches |

👉 Works dynamically for any position (e.g. `top_2`, `top_5`, etc.)

---

### ♟️ Last Match

| Placeholder | Description |
|------------|------------|
| `%chesscraft_last_result%` | win / loss / draw |
| `%chesscraft_last_opponent%` | Opponent username |
| `%chesscraft_last_opponent_displayname%` | Opponent display name |
| `%chesscraft_last_elo_change%` | Elo change |
| `%chesscraft_last_elo_after%` | Player Elo after match |
| `%chesscraft_last_opponent_elo_after%` | Opponent Elo after match |
| `%chesscraft_last_side%` | white / black |
| `%chesscraft_last_type%` | pvp / cpu |
| `%chesscraft_last_updated%` | Timestamp of match |
| `%chesscraft_last_moves_count%` | Number of moves |

---

## 🔧 Requirements

- ✅ Paper **1.21.4** *(tested)*
- ⚠️ Other versions may work but are untested
- ✅ [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- ✅ [ChessCraft](https://github.com/jpenilla/chesscraft)

---

## 🚀 Installation

1. Download the latest release
2. Place the `.jar` file into your `/plugins` folder
3. Restart your server
4. Run: /papi reload

---

## 💡 Example
♔ Chess Stats ♔

Elo: %chesscraft_elo%
Rank: %chesscraft_rank%

Last Match:
%chesscraft_last_result% vs %chesscraft_last_opponent%
(+%chesscraft_last_elo_change%)

