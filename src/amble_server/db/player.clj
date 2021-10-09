(ns amble-server.db.player
  (:require [amble-server.db.player-sql :as player-sql])
  (:refer-clojure :exclude [find]))

(def success ::success)
(def failure ::failure)

(defn add! [game-id, id]
  (try (let [row-add-count  (player-sql/add! game-id, id)]
         (when (not (= 1 row-add-count))
           (throw (new IllegalStateException (format "Bad db result \"%s\"."
                                                     row-add-count))))
         {success id})
       (catch Throwable e
         {failure e})))

(defn find-all [game-id]
  (try (let [player-ids (vec (for [player-row (player-sql/find game-id)]
                               (:id player-row)))]
         {success player-ids})
       (catch Throwable e
         {failure e})))

(defn find
  ([game-id]
   (find-all game-id))
  ([game-id, id]
   (try (let [player-id (first (for [player-row (player-sql/find game-id, id)]
                                 (:id player-row)))]
          {success player-id})
        (catch Throwable e
          {failure e}))))
