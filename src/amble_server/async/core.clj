(ns amble-server.async.core)

(def game-subscribers-atom (atom []))

(defn post-move!
  "Asynchronously send out move notification to all game subscribers."
  [game-id, player-id, move-id]
  (for [subscriber [(deref game-subscribers-atom)]]
    (notify-subscriber! subscriber)))

(defn add-game-subscriber! [game-id, player-id]
  (for [subscriber [(deref game-subscribers-atom)]]
    (notify-subscriber! subscriber)))