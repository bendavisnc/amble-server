(ns amble-server.db.game
  (:require [amble-server.db.game-sql :as game-sql]))

(def success ::sucess)
(def failure ::failure)

(defn create!
  [id]
  (try (let [db-result
             (game-sql/create! id)]
         (if (not (pos? db-result))
           {failure (new IllegalStateException (format "Bad db result \"%s\".",
                                                       db-result))}
           {success id}))
       (catch Throwable e
         {failure e})))

(defn find [id]
  (try (let [found*
             (game-sql/find id)
             found
             (first (map :id found*))]
         {success found})
       (catch Throwable e
         {failure e})))

(defn delete!
  "Deletes the game with the given game id."
  [id]
  (try (let [db-result
             (game-sql/delete! id)]
         (if (not (pos? db-result))
           {failure (new IllegalStateException (format "Bad db result \"%s\".",
                                                       db-result))}
           {success id}))
       (catch Throwable e
         {failure e})))


