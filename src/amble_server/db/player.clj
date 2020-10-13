(ns amble-server.db.player
  (:require
    [clojure.java.jdbc :as jdbc]
    [amble-server.db.core :as db]))

(defn find
  ([game-id]
   (let [found
         (jdbc/query db/db ["select * from player where gameId = ?" game-id])]
     (map :id found)))
  ([game-id, id]
   (let [found
         (jdbc/query db/db ["select * from player where gameId = ? and id = ?" game-id id])]
     (first (map :id found)))))


(defn add!
  [game-id, id]
  (let [write
        (jdbc/insert! db/db :player {:id id, :gameId game-id})]
    (first (map (fn [w]
                  (let [k (first (keys w))]
                    (k w)))
                write))))
