(ns amble-server.db.player
  (:refer-clojure :exclude [find])
  (:require
    [amble-server.db.player-sql :as player-sql]))

(def success ::success)
(def failure ::failure)

(defn add!
  [game-id id]
  (try (let [row-add-count (player-sql/add! (name game-id)
                                            (name id))]
         (when (not (= 1 row-add-count))
           (throw (new IllegalStateException
                       (format "Bad db result \"%s\"."
                               row-add-count))))
         {success id})
       (catch Throwable e
         {failure e})))

(defn find-all
  [game-id]
  (try (let [player-ids (vec (for [player-row (player-sql/find (name game-id))]
                               (keyword (:id player-row))))]
         {success player-ids})
       (catch Throwable e
         {failure e})))

(defn find
  ([game-id]
   (find-all game-id))
  ([game-id id]
   (try (let [player-id (first (for [player-row (player-sql/find (name game-id)
                                                                 (name id))]
                                 (:id player-row)))]
          {success (keyword player-id)})
        (catch Throwable e
          {failure e}))))
