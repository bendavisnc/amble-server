(ns amble-server.resource.game
  (:require
   [amble-server.db.game :as game-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(declare inform)

(def log (. LogManager getLogger "amble-server.resource.game"))

(defn create! [id]
  (let [db-result (game-db/create! id)
        db-result-type (first (keys db-result))
        db-result-value (first (vals db-result))]
    (or (and (= game-db/failure db-result-type)
             (or (and (= nil db-result-value)
                      (do (.info log "Game db write produced nil result.")
                          (.info log (str "  "
                                          [id, db-result-type, db-result-value]))
                          nil))
                 (and (instance? Throwable db-result-value)
                      (do (.info log "Game db write produced error result.")
                          (.info log (str "  "
                                          [id, db-result-type, db-result-value]))
                          (throw db-result-value)))))
        (and (= game-db/success db-result-type)
             db-result-value))))

(defn get [id]
  (let [db-result (game-db/find id)
        db-result-type (first (keys db-result))]
    (cond (= game-db/failure db-result-type)
          (if (nil? (game-db/failure db-result))
            (do (.info log "Game not found.")
                (.info log (str "  "
                                [id]))
                nil)
            ;;else
            (do (.info log "Game resource get is being thrown from bad db result.")
                (.info log (str "  "
                                [id, (game-db/failure db-result)]))
                (throw (game-db/failure db-result))))

          (= game-db/success db-result-type)
          (game-db/success db-result))))

(defn delete! [id]
  (let [game (game-db/delete! id)]
    (if (nil? game)
      (do (.info log "Game not deleted.")
          (.info log (str "  "
                          [id])))
      ;;else
      (do (.info log "Game deleted.")
          (.info log (str "  "
                          [id]))))
    game))

