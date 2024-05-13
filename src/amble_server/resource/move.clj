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

(defn delete! [game-id, id]
  (let [delete (move-db/delete! game-id, id)
        delete-successful? (= move-db/success 
                           (first (keys delete)))]
    (if delete-successful?
      (move-db/success delete)
      (throw (new Exception (str delete))))))

(defn get [game-id, id]
  (let [find (move-db/find game-id, id) 
        find-successful? (= move-db/success 
                            (first (keys find)))]
    (if find-successful?
      (move-db/success find)
      (throw (new Exception (str find))))))

(defn get-by-rowid [rowid]
  (let [
        find (move-db/find-by-rowid rowid) 
        ;; find (move-db/find "TheSaturdayGame" 0) 
        find-successful? (= move-db/success 
                            (first (keys find)))]
    (if find-successful?
      (move-db/success find)
      (throw (new Exception (str find))))))

(defn get-all [game-id]
  (let [find (move-db/find-by-game-id game-id) 
        find-successful? (= move-db/success 
                            (first (keys find)))]
    (if find-successful?
      (move-db/success find)
      (throw (new Exception (str find))))))

(defn get-by-player-id [game-id, player-id]
  (let [
        find (move-db/find-by-player-id game-id, player-id) 
        ;; find (move-db/find "TheSaturdayGame" 0) 
        find-successful? (= move-db/success 
                            (first (keys find)))]
    (if find-successful?
      (move-db/success find)
      (throw (new Exception (str find))))))



