# ♟️ ChessCraft Placeholders

A simple **PlaceholderAPI addon** for [ChessCraft](https://github.com/jpenilla/chesscraft)  
that provides Elo, rank, leaderboard and match-related placeholders.

---

## ✨ Features

- 📊 Player Elo & peak Elo
- 🏆 Player rank system (matches ChessCraft leaderboard)
- 📈 Top leaderboard placeholders
- 🧠 Last match information (result, opponent, Elo change)
- ⚡ Works directly with ChessCraft database (no config required)

---

## 📦 Placeholders

### 👤 Player
| Placeholder | Description |
|------------|------------|
| `%chesscraft_elo%` | Shows the player's current Elo |
| `%chesscraft_peak_elo%` | Shows the player's highest reached Elo |
| `%chesscraft_rank%` | Shows the player's rank |
| `%chesscraft_rated_matches%` | Shows the number of rated matches played |

---

### 🏆 Leaderboard
| Placeholder | Description |
|------------|------------|
| `%chesscraft_top_1%` → `%chesscraft_top_10%` | Shows top players with Elo |

---

### ♟️ Last Match
| Placeholder | Description |
|------------|------------|
| `%chesscraft_last_result%` | Shows result of last match (win/loss/draw) |
| `%chesscraft_last_opponent%` | Shows last opponent (or CPU) |
| `%chesscraft_last_elo_change%` | Shows Elo change from last match |

---

## 🔧 Requirements

- ✅ Paper **1.21.4** *(tested)*
- ⚠️ Other versions may work but are untested
- ✅ [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- ✅ [ChessCraft](https://github.com/jpenilla/chesscraft)

---

## 🚀 Installation

1. Download the latest release
2. Put the `.jar` file into your `/plugins` folder
3. Restart your server
4. Run:
   /papi reload

---

## 💡 Example
♔ Chess Leaderboard ♔

%chesscraft_top_1%
%chesscraft_top_2%
%chesscraft_top_3%


---

## ⚠️ Important

- This plugin is **not affiliated with ChessCraft**
- It only reads data from the ChessCraft database
- Make sure ChessCraft is installed and working

---

## 📜 License

This project is licensed under the MIT License.

---

## ❤️ Credits

- [ChessCraft](https://github.com/jpenilla/chesscraft) by jpenilla
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)

---

## 🛠️ Future Ideas

- Match history placeholders
- Win/Loss/Draw statistics
- Configurable formats (colors, prefixes, etc.)
- Caching system for better performance

---

Made with ❤️ by NoHope
