(ns amble-server.resource.move
  (:require
   [amble-server.db.move :as move-db]))

(defn add! [game-id, player-id, id, move]
  (println [player-id, id, game-id])
  (println (str (format "Adding player's, \"%s\", move, \"%s\", to game, \"%s\".",
                        player-id,
                        id,
                        game-id))) ;; todo, reword
  (move-db/add! game-id, player-id, id, move)
  id)

