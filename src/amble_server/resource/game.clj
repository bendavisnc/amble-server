(ns amble-server.resource.game
  (:require
   [amble-server.db.game :as game-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.resource.game"))

(defn get [id]
  (let [game (game-db/find id)]
    (if (nil? game)
      (do (.info log "Game is not found.")
          (.info log (str "  - "
                          [id])))
      ;;else
      (do (.info log "Game found.")
          (.info log (str "  - "
                          [id]))))
    game))

  ;(inform "Getting" id)
  ;(game-db/find id))

(defn create! [id]
  (let [game (game-db/create! id)]
    (if (nil? game)
      (do (.info log "Game not created.")
          (.info log (str "  - "
                          [id])))
      ;;else
      (do (.info log "Game created.")
          (.info log (str "  - "
                          [id]))))
    game))

;(defn delete! [id]
;  (inform "Deleting", id)
;  (game-db/delete! id))

(defn delete! [id]
  (let [game (game-db/delete! id)]
    (if (nil? game)
      (do (.info log "Game not deleted.")
          (.info log (str "  - "
                          [id])))
      ;;else
      (do (.info log "Game deleted.")
          (.info log (str "  - "
                          [id]))))
    game))

