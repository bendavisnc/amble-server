(ns amble-server.resource.player
  (:require
    [amble-server.db.player :as player-db]))

(defn add! [game-id, id]
  (println (str "Adding player, " id ", to game, " game-id "."))
  (player-db/add! game-id id))

