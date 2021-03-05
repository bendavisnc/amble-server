(ns amble-server.db.player
  (:require [amble-server.db.player-sql :as player-sql]))

(def success ::sucess)
(def failure ::failure)

(defn add!
  [game-id, id]
  (try (let [db-result
             (player-sql/add! game-id, id)]
         (if (not (pos? db-result))
           {failure (new IllegalStateException (format "Bad db result \"%s\".",
                                                       db-result))}
           {success id}))
       (catch Throwable e
         {failure e})))

(defn find
  ([game-id]
   (try (let [found*
              (player-sql/find game-id)
              found
              (map :id found*)]
          {success found})
        (catch Throwable e
          {failure e})))
  ([game-id, id]
   (try (let [found*
              (player-sql/find game-id, id)
              found
              (first (map :id found*))]
          {success found})
        (catch Throwable e
          {failure e}))))
