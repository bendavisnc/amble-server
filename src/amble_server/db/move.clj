(ns amble-server.db.move
  (:require
   [amble-server.db.move-sql :as move-sql])
  (:import
   [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.db.move"))

(def success ::sucess)
(def failure ::failure)

(defn add!
  [game-id, player-id, id, move]
  (try (let [db-result
             (move-sql/add! game-id, player-id, id, move)]
         (if (not (pos? db-result))
           {failure (new IllegalStateException (format "Bad db result \"%s\".",
                                                       db-result))}
           {success id}))
       (catch Throwable e
         {failure e})))

(defn find [game-id, player-id, id]
  (try (let [found
             (move-sql/find game-id, player-id, id)]
         {success found})
       (catch Throwable e
         {failure e})))

