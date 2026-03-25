# ♟️ ChessCraft Placeholders

![Version](https://img.shields.io/badge/version-1.2.0-blue)
![Platform](https://img.shields.io/badge/platform-Paper-ff9f43)
![API](https://img.shields.io/badge/PlaceholderAPI-required-orange)
![API](https://img.shields.io/badge/ChessCraft-required-orange)
![Status](https://img.shields.io/badge/status-active-success)

A **PlaceholderAPI addon** for [ChessCraft](https://github.com/jpenilla/chesscraft)  
providing player stats, leaderboard data and match information.

---

## ✨ Features

- 📊 Player stats (Elo, peak Elo, rank, winrate, streaks)
- 🏆 Leaderboard placeholders
- ♟️ Last match data
- 📜 Full match history
- ⚡ Direct database access (no API dependency)
- 🚀 Built-in caching for better performance

---

## 📦 Placeholders

👉 Full list: [PLACEHOLDERS.md](./PLACEHOLDERS.md)

### Example Placeholders

- `%chesscraft_elo%`
- `%chesscraft_rank%`
- `%chesscraft_winrate%`
- `%chesscraft_last_result%`
- `%chesscraft_top_1%`

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
(%chesscraft_last_elo_change%)

---

## 🧠 Notes

- Elo after match = `elo + elo_change`
- All values are player-perspective based
- Data is cached to reduce database load