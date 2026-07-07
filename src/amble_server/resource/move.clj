(ns amble-server.resource.move
  "Services moves to the api from the db."
  (:refer-clojure :exclude [get])
  (:require
    [amble-server.db.move :as move-db]))

(defn add!
  [game-id player-id player-piece-index move x y client-id]
  (let [add
        (move-db/add! game-id player-id player-piece-index move x y client-id)]
    (if-let [successful-add (move-db/success add)]
      successful-add
      (throw (ex-info
              "An unexpected occurred while trying to add move."
              {:game-id   game-id
               :player-id player-id
               :client-id client-id
               :db-result add})))))

(defn delete!
  [game-id id]
  (let [delete (move-db/delete! game-id id)]
    (if-let [successful-delete (move-db/success delete)]
      successful-delete
      (throw (ex-info
              "An unexpected error occurred while trying to delete move."
              {:game-id   game-id
               :id        id
               :db-result delete})))))

(defn get
  [game-id id]
  (let [find (move-db/find game-id id)]
    (if-let [successful-find (move-db/success find)]
      successful-find
      (throw (ex-info
              "An unexpected error occurred while trying to find move."
              {:game-id   game-id
               :id        id
               :db-result find})))))

(defn get-by-rowid
  [rowid]
  (let [find (move-db/find-by-rowid rowid)]
    (if-let [successful-find (move-db/success find)]
      successful-find
      (throw
       (ex-info
        "An unexpected error occurred while trying to find move by `rowid`."
        {:rowid     rowid
         :db-result find})))))

(defn get-all
  [game-id]
  (let [find (move-db/find-by-game-id game-id)]
    (if-let [successful-find (move-db/success find)]
      successful-find
      (throw
       (ex-info
        "An unexpected error occurred while trying to find moves by `game-id`."
        {:game-id   game-id
         :db-result find})))))


(defn get-by-player-id
  [game-id player-id]
  (let [find (move-db/find-by-player-id game-id player-id)]
    (if-let [successful-find (move-db/success find)]
      successful-find
      (throw
       (ex-info
        "An unexpected error occurred while trying to find moves by `player-id`."
        {:game-id   game-id
         :player-id player-id
         :db-result find})))))

