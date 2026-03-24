# ♟️ ChessCraft Placeholders

![Version](https://img.shields.io/badge/version-1.0-blue)
![Platform](https://img.shields.io/badge/platform-Paper-ff9f43)
![API](https://img.shields.io/badge/PlaceholderAPI-required-orange)
![Status](https://img.shields.io/badge/status-active-success)

A **PlaceholderAPI addon** for [ChessCraft](https://github.com/jpenilla/chesscraft)  
providing player stats, leaderboard data and match information.

---

## ✨ Features

- 📊 Player stats (Elo, peak Elo, rank, matches)
- 🏆 Leaderboard placeholders
- ♟️ Last match data
- 📜 Match history *(WIP)*
- ⚡ Direct database access (no API dependency)

---

## 📦 Placeholders

### 👤 Player
`%chesscraft_username%`  
`%chesscraft_displayname%`  
`%chesscraft_elo%`  
`%chesscraft_peak_elo%`  
`%chesscraft_rank%`  
`%chesscraft_rated_matches%`

---

### 🏆 Leaderboard
`%chesscraft_top_1%` → `%chesscraft_top_10%`

Fields:
- `_name`
- `_displayname`
- `_elo`
- `_peak_elo`
- `_rated_matches`

---

### ♟️ Last Match
`%chesscraft_last_result%`  
`%chesscraft_last_opponent%`  
`%chesscraft_last_opponent_displayname%`  
`%chesscraft_last_elo_change%`  
`%chesscraft_last_elo_after%`  
`%chesscraft_last_opponent_elo_after%`  
`%chesscraft_last_side%`  
`%chesscraft_last_type%`  
`%chesscraft_last_updated%`  
`%chesscraft_last_moves_count%`

---

### 📜 History *(WIP)*
`%chesscraft_history_1_result%`  
`%chesscraft_history_1_opponent%`  
`%chesscraft_history_1_opponent_displayname%`  
`%chesscraft_history_1_elo_change%`

→ Works for any index (`history_1`, `history_2`, ...)

---

## 🔧 Requirements

- Paper **1.21.4** *(tested)*
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [ChessCraft](https://github.com/jpenilla/chesscraft)

---

## 🚀 Installation

1. Download latest release
2. Put `.jar` into `/plugins`
3. Restart server
4. `/papi reload`

---

## 💡 Example
♔ Chess Stats ♔

Elo: %chesscraft_elo%
Rank: %chesscraft_rank%

Last Match:
%chesscraft_last_result% vs %chesscraft_last_opponent%
(+%chesscraft_last_elo_change%)

---

## 🧠 Notes

- Elo after match = `elo + elo_change`
- All values are player-perspective based
- History system is still expanding