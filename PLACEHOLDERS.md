# ♟️ ChessCraft Placeholders

Complete list of all available placeholders.

---

## 👤 Player

- `%chesscraft_username%`
- `%chesscraft_displayname%`
- `%chesscraft_elo%`
- `%chesscraft_peak_elo%`
- `%chesscraft_rank%`
- `%chesscraft_rated_matches%`

---

## 📊 Stats

- `%chesscraft_wins%`
- `%chesscraft_losses%`
- `%chesscraft_draws%`
- `%chesscraft_winrate%`

- `%chesscraft_pvp_wins%`
- `%chesscraft_cpu_wins%`

---

## 🔥 Streaks

- `%chesscraft_current_win_streak%`
- `%chesscraft_current_loss_streak%`
- `%chesscraft_best_win_streak%`
- `%chesscraft_best_loss_streak%`

---

## 📈 Elo Stats

- `%chesscraft_total_elo_gain%`
- `%chesscraft_total_elo_loss%`
- `%chesscraft_best_elo_gain%`
- `%chesscraft_worst_elo_loss%`
- `%chesscraft_average_elo_change%`

---

## ♟️ Last Match

- `%chesscraft_last_result%`
- `%chesscraft_last_opponent%`
- `%chesscraft_last_opponent_displayname%`
- `%chesscraft_last_elo_change%`
- `%chesscraft_last_elo_after%`
- `%chesscraft_last_opponent_elo_after%`
- `%chesscraft_last_side%`
- `%chesscraft_last_type%`
- `%chesscraft_last_updated%`
- `%chesscraft_last_moves_count%`

---

## 🏆 Leaderboard

### Basic
- `%chesscraft_top_1%`
- `%chesscraft_top_2%`
- ...
- `%chesscraft_top_10%`

### Fields

- `%chesscraft_top_<pos>_name%`
- `%chesscraft_top_<pos>_displayname%`
- `%chesscraft_top_<pos>_elo%`
- `%chesscraft_top_<pos>_peak_elo%`
- `%chesscraft_top_<pos>_rated_matches%`

Example:
%chesscraft_top_1_name%
%chesscraft_top_1_elo%

---

## 📜 Match History

Index-based (1 = latest match)

### Base
- `%chesscraft_history_1_result%`
- `%chesscraft_history_1_opponent%`
- `%chesscraft_history_1_opponent_displayname%`
- `%chesscraft_history_1_elo_change%`
- `%chesscraft_history_1_elo_after%`
- `%chesscraft_history_1_opponent_elo_after%`
- `%chesscraft_history_1_side%`
- `%chesscraft_history_1_type%`
- `%chesscraft_history_1_updated%`
- `%chesscraft_history_1_moves_count%`

### Summary
- `%chesscraft_history_1_summary%`

Example:
%chesscraft_history_1_summary%

Works with any index:
%chesscraft_history_2_result%
%chesscraft_history_3_summary%

---

## 🧩 Notes

- `result` → `win`, `loss`, `draw`
- `type` → `pvp`, `cpu`
- `side` → `white`, `black`
- Elo change includes `+` automatically