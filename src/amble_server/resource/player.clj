(ns amble-server.resource.player
  (:require
    [amble-server.db.player :as player-db]))

(defn add! [game-id, id]
  (println (str "Adding player, " id ", to game, \"" game-id "\"."))
  (player-db/add! game-id id))

(defn get-all [game-id]
  (println (str "Getting all players for game, \""  game-id "\"."))
  (player-db/find game-id))

(defn get [game-id, id]
  (println (str "Getting player, " id ", on game, \"" game-id "\"."))
  (let [somethingToRevisit (player-db/find game-id id)]
    ;(println "unsure")
    ;(println somethingToRevisit)
    somethingToRevisit))

