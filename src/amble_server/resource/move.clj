(ns amble-server.resource.move
  (:require [amble-server.db.move :as move-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.resource.move"))

(defn add! [game-id, player-id, id, move]
  (let [move-db-result (move-db/add! game-id, player-id, id, move)]
    (if (nil? move-db-result)
      (do (.info log "Player's move not added to game.")
          (.info log (str "  - "
                          [player-id, id, game-id])))
      ;;else
      (do (.info log "Player's move added to game.")
          (.info log (str "  - "
                          [player-id, id, game-id]))))

    id))
