(ns amble-server.resource.player
  (:require [amble-server.db.player :as player-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.resource.player"))

(defn add! [game-id, id]
  (.info log (str "Adding player, \"" id \" ", to game, \"" game-id "\"."))
  (player-db/add! game-id id))

(defn get-all [game-id]
  (.info log (str "Getting all players for game, \"" game-id "\"."))
  (player-db/find game-id))

(defn get [game-id, id]
  (.info log (str "Getting player, \"" id \" ", on game, \"" game-id "\"."))
  (player-db/find game-id id))
