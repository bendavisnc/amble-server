(ns amble-server.resource.game
  (:require
    [amble-server.db.game :as game-db])
  (:import
    (org.apache.logging.log4j LogManager Logger)))

(def log (. LogManager getLogger "amble-server.resource.game"))

(defn create!
  [id]
  (let [db-result (game-db/create! id)]
    (cond
      (game-db/failure db-result)
      (do
        (.info log "Error occured while trying to create game.")
        (throw (new RuntimeException (game-db/failure db-result))))
      (= {game-db/success id} db-result)
      (do
        (.info log "Game created!")
        (.info log (format "  \"%s\"" (name id)))
        (game-db/success db-result))

      :default
      (do
        (.info log
               "The unexpected occurred while trying to create game."
               (.info log (format "  \"%s\"" db-result))
               (throw (new RuntimeException db-result)))))))

(defn get
  [id]
  (assert (keyword? id)
          (str "Bad id value provided during get, \"" id "\"."))
  (let [db-result        (game-db/find id)
        nil-result-value nil]
    (cond
      (game-db/failure db-result)
      (do
        (.info log "Error occured while trying to get game.")
        (throw (new RuntimeException (game-db/failure db-result))))
      (= {game-db/success nil-result-value} db-result)
      (do
        (.info log "Game not found!")
        (.info log (format "  \"%s\"" (name id)))
        nil-result-value)
      (= {game-db/success id} db-result)
      (do
        (.info log "Game found!")
        (.info log (format "  \"%s\"" (name id)))
        (game-db/success db-result))
      :default
      (do
        (.info log "The unexpected occurred while trying to get game.")
        (.info log (format "  \"%s\"" db-result))
        (throw (new RuntimeException db-result))))))

(defn delete!
  [id]
  (let [db-result (game-db/delete! id)]
    (if (game-db/failure db-result)
      (do (.info log "Game not deleted.")
          (.info log (format "  \"%s\"" db-result))
          (throw (game-db/failure db-result)))
      ;; else
      (if (= {game-db/success id}
             db-result)
        (do (.info log "Game deleted.")
            (.info log (format "  \"%s\"" (name id)))
            id)
        ;; else
        (do (.info log "No game deleted.")
            (.info log (format "  \"%s\"" db-result))
            "")))))
