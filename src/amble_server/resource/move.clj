(ns amble-server.resource.move
  (:require [amble-server.db.move :as move-db]
            [amble-server.db.player :as player-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.resource.move"))

(defn add! [game-id, player-id, id, move]
  (let [db-result (move-db/add! game-id, player-id, id, move)
        db-result-type (first (keys db-result))]
    (cond (= move-db/failure db-result-type)
          (if (nil? (move-db/failure db-result))
            (do (.info log "Move not added for unknown, nil reason.")
                (.info log (str "  "
                                [game-id, id]))
                nil)
            ;;else
            (do (.info log "Player resource add is being thrown from bad db result.")
                (.info log (str "  "
                                [game-id, id, (move-db/failure db-result)]))
                (throw (move-db/failure db-result))))

          (= move-db/success db-result-type)
          (move-db/success db-result))))

(defn get [game-id, player-id, id]
  (let [db-result (move-db/find game-id, player-id, id)
        db-result-type (first (keys db-result))]
    (cond (= move-db/failure db-result-type)
          (if (nil? (move-db/failure db-result))
            (do (.info log "Move not found.")
                (.info log (str "  "
                                [game-id, player-id, id]))
                nil)
            ;;else
            (do (.info log "Move resource get is being thrown from bad db result.")
                (.info log (str "  "
                                [game-id, player-id, id, (move-db/failure db-result)]))
                (throw (move-db/failure db-result))))

          (= move-db/success db-result-type)
          (move-db/success db-result))))
