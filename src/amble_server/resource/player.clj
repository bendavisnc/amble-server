(ns amble-server.resource.player
  (:require [amble-server.db.player :as player-db]
            [amble-server.db.game :as game-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager])
  (:refer-clojure :exclude [get]))

(def log (. LogManager getLogger "amble-server.resource.player"))

(defn add! [game-id, player-id]
  (first (mapcat (fn [_] ;; a found game
                   (mapcat
                    (fn [_] ;; player id that currently doesn't exist
                      (let [db-result (player-db/add! game-id, player-id)]
                        (cond
                          (player-db/failure db-result)
                          (do
                            (.info log "Error occured while trying to add player to game.")
                            (throw (new RuntimeException (player-db/failure db-result))))
                          (player-db/success db-result)
                          (do
                            (.info log "Player added to game!")
                            (.info log (format "  \"%s\"  \n\"%s\\\"" game-id, player-id))
                            (player-db/success db-result))
                          :default
                          (do
                            (.info log "The unexpected occurred while trying to add player to game."
                                   (.info log (format "  \"%s\"" db-result))
                                   (throw (new RuntimeException db-result)))))))
                    (if-let [_ (-> (player-db/find game-id, player-id)
                                   player-db/success)]
                      (do
                        (.info log "Cannot add player that already exists.")
                        (.info log (format "  \"%s\"  \n\"%s\\\"" game-id, player-id))
                        [])
                      (do
                        (.info log "Can add player that currently does not exist.")
                        (.info log (format "  \"%s\"  \n\"%s\\\"" game-id, player-id))
                        [player-id]))))
                 (if-let [gif (-> game-id
                                  game-db/find
                                  game-db/success)]
                   [gif]
                   (do
                     (.info log "No game found by id to add player.")
                     (.info log (format "  \"%s\"  \n\"%s\\\"" game-id, player-id))
                     [])))))

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

(defn get [game-id, player-id]
  (first
   (for [game-db-find [(game-db/find game-id)]
         :when (or (= {game-db/success game-id} game-db-find)
                   (do
                     (.info log "No game found for player.")
                     (.info log (format "  \"%s\"  \n\"%s\\\"" game-id, player-id))
                     nil))
         player-db-find [(player-db/find game-id player-id)]
         :when (or (= {player-db/success player-id} player-db-find)
                   (do
                     (.info log "No player found for game.")
                     (.info log (format "  \"%s\"  \n\"%s\\\"" game-id, player-id)))
                   nil)]
     player-id)))
