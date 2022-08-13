(ns amble-server.resource.move
  "Services moves to the api from the db."
  (:require [amble-server.db.move :as move-db])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.resource.move"))

(defn add! [game-id, player-id, player-piece-index, move, x, y, client-id]
  (let [add (move-db/add! game-id, player-id, player-piece-index, move, x, y, client-id)
        add-successful? (= move-db/success 
                           (first (keys add)))]
    (if add-successful?
      (move-db/success add)
      (throw (new Exception (str add))))))

(defn get [game-id, id]
  (let [find (move-db/find game-id, id) 
        _ (println "buttt")
        _ (println find)
        find-successful? (= move-db/success 
                           (first (keys find)))]
    (if find-successful?
      (move-db/success find)
      (throw (new Exception (str find))))))

