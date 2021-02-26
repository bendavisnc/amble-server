(ns amble-server.resource.player
  (:require [amble-server.db.player :as player-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.resource.player"))

(defn add! [game-id, id]
  (player-db/add! game-id id)
  (.info log "Game has new added player.")
  (.info log (str "  "
                  [game-id, id]))
  id)


(defn get-all [game-id]
  (let [game-players (player-db/find game-id)]
    (if (empty? game-players)
      (do (.info log "Game has no players.")
          (.info log (str "  "
                          [game-id])))
      ;;else
      (do (.info log "Game has players found.")
          (.info log (str "  "
                          [game-id, game-players]))))
    game-players))



(defn get [game-id, id]
  (let [game-player (player-db/find game-id id)]
    (if (nil? game-player)
      (do (.info log "Game has no player with such id.")
          (.info log (str "  "
                          [game-id, id])))
      ;;else
      (do (.info log "Game has player found.")
          (.info log (str "  "
                          [game-id, game-player]))))
    game-player))
