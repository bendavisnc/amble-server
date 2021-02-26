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

  ;(.debug log [player-id, id, game-id])
  ;(.info log "Adding player move to game.")
  ;(.info log [player-id, id, game-id]) ;; todo, reword
  ;(move-db/add! game-id, player-id, id, move)
  ;id)

