(ns amble-server.resource.game
  (:require
    [amble-server.db.game :as game-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(declare inform)

(def log (. LogManager getLogger "amble-server.resource.game"))

(defn create! [id]
  (let [db-result (game-db/create! id)]
    (cond
      (= [game-db/failure] (keys db-result))
      (do
        (.info log "Error occured while trying to create game.")
        (throw (new RuntimeException (game-db/failure db-result))))
      (= {game-db/success id} db-result)
      (do
        (.info log "Game created!")
        (.info log (format "  (\"%s\")" id))
        (game-db/success db-result))

      :default
      (do
        (.info log "The unexpected occurred while trying to create game."
               (.info log (format "  \"%s\"" db-result))
               (throw (new RuntimeException db-result)))))))




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
  (let [db-result (game-db/delete! id)]
    (if (game-db/failure db-result)
      (do (.info log "Game not deleted.")
          (.info log (format "  \"%s\"" db-result))
          (throw (game-db/failure db-result)))
      ;;else
      (if (= {game-db/success id}
             db-result)
        (do (.info log "Game deleted.")
            (.info log (format "  \"%s\"" id))
            id)
        ;;else
        (do (.info log "No game deleted.")
            (.info log (format "  \"%s\"" db-result))
            "")))))


