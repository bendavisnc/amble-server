(ns amble-server.db.game
  (:refer-clojure :exclude [find])
  (:require
   [amble-server.db.game-sql :as game-sql]))

(def success ::success)
(def failure ::failure)

(defn create! [id]
  (try (let [row-add-count  (game-sql/create! (name id))]
         (when (not (= 1 row-add-count))
           (throw (new IllegalStateException (format "Bad db result \"%s\"."
                                                     row-add-count))))
         {success (keyword id)})
       (catch Throwable e
         {failure e})))

(defn find [id]
  (try (let [game-id-found (first (for [game-row (game-sql/find (name id))]
                                    (:id game-row)))]
         {success (keyword game-id-found)})
       (catch Throwable e
         {failure e})))

(defn delete!
  "Deletes the game with the given game id."
  [id]
  (try (let [row-delete-count  (game-sql/delete! (name id))]
         (when (not (= 1 row-delete-count))
           (throw (new IllegalStateException (format "Bad db result \"%s\"."
                                                     row-delete-count))))
         {success (keyword id)})
       (catch Throwable e
         {failure e})))
