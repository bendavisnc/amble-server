(ns amble-server.db.move
  (:require
   [amble-server.db.move-sql :as move-sql])
  (:import
   [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.db.move"))

(def success ::success)
(def failure ::failure)

(defn add!
  [game-id, player-id, player-piece-index, move, x, y, client-id]
  (try (let [[{:keys [count]}] (move-sql/count game-id)
             add (move-sql/add! game-id, player-id, player-piece-index, count, (str move), x, y, client-id)]
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

(defn find-by-rowid [rowid]
  (try (let [found
             (move-sql/find-by-rowid rowid)]
         {success found})
       (catch Throwable e
         {failure e})))



