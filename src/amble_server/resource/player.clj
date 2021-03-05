(ns amble-server.resource.player
  (:require [amble-server.db.player :as player-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.resource.player"))

(defn add! [game-id, id]
  (let [db-result (player-db/add! game-id, id)
        db-result-type (first (keys db-result))]
    (cond (= player-db/failure db-result-type)
          (if (nil? (player-db/failure db-result))
            (do (.info log "Player not added for unknown, nil reason.")
                (.info log (str "  "
                                [game-id, id]))
                nil)
            ;;else
            (do (.info log "Player resource add is being thrown from bad db result.")
                (.info log (str "  "
                                [game-id, id, (player-db/failure db-result)]))
                (throw (player-db/failure db-result))))

          (= player-db/success db-result-type)
          (player-db/success db-result))))

(defn get-all [game-id]
  (let [db-result (player-db/find game-id)
        db-result-type (first (keys db-result))]
    (cond (= player-db/failure db-result-type)
          (if (nil? (player-db/failure db-result))
            (do (.info log "Players not found.")
                (.info log (str "  "
                                [game-id]))
                nil)
            ;;else
            (do (.info log "Player resource get all is being thrown from bad db result.")
                (.info log (str "  "
                                [game-id, (player-db/failure db-result)]))
                (throw (player-db/failure db-result))))

          (= player-db/success db-result-type)
          (player-db/success db-result))))

(defn get [game-id, id]
  (let [db-result (player-db/find game-id, id)
        db-result-type (first (keys db-result))]
    (cond (= player-db/failure db-result-type)
          (if (nil? (player-db/failure db-result))
            (do (.info log "Player not found.")
                (.info log (str "  "
                                [id]))
                nil)
            ;;else
            (do (.info log "Player resource get is being thrown from bad db result.")
                (.info log (str "  "
                                [id, (player-db/failure db-result)]))
                (throw (player-db/failure db-result))))

          (= player-db/success db-result-type)
          (player-db/success db-result))))
