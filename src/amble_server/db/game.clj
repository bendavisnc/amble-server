(ns amble-server.db.game
  (:require [amble-server.db.game-sql :as game-sql])
  (:refer-clojure :exclude [find]))

(def success ::sucess)
(def failure ::failure)

(defn create! [id]
  (try (let [row-add-count  (game-sql/create! id)]
         (when (not (= 1 row-add-count))
           (throw (new IllegalStateException (format "Bad db result \"%s\"."
                                                     row-add-count))))
         {success id})
       (catch Throwable e
         {failure e})))

(defn find [id]
  (try (let [game-id-found (first (for [game-row (game-sql/find id)]
                                    (:id game-row)))]
         {success game-id-found})
       (catch Throwable e
         {failure e})))

(defn delete!
  "Deletes the game with the given game id."
  [id]
  (try (let [row-delete-count  (game-sql/delete! id)]
         (when (not (= 1 row-delete-count))
           (throw (new IllegalStateException (format "Bad db result \"%s\"."
                                                     row-delete-count))))
         {success id})
       (catch Throwable e
         {failure e})))

