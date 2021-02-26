(ns amble-server.resource.move
  (:require [amble-server.db.move :as move-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.resource.move"))

(defn add! [game-id, player-id, id, move]
  (.debug log [player-id, id, game-id])
  (.info log (str (format "Adding player's, \"%s\", move, \"%s\", to game, \"%s\".",
                          player-id,
                          id,
                          game-id))) ;; todo, reword
  (move-db/add! game-id, player-id, id, move)
  id)

