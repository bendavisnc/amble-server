(ns amble-server.db.move
  (:require
    [amble-server.db.move-sql :as move-sql])
  (:import
    [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.db.move"))

(def success ::sucess)
(def failure ::failure)

(defn add!
  [game-id, player-id, player-piece-index, move]
  (try (let [[{:keys [count]}] (move-sql/count game-id)
             [x, y] (last move)
             add (move-sql/add! game-id, player-id, player-piece-index, count, (str move), x, y)]
         (if (not (pos? add))
           {failure (new IllegalStateException (format "Bad db result \"%s\".",
                                                       add))}
           {success count}))
       (catch Throwable e
         {failure e})))

(defn find [game-id, id]
  (try (let [found
             (move-sql/find game-id, id)]
         {success found})
       (catch Throwable e
         {failure e})))

  
