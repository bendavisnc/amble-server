(ns amble-server.db.move
  (:require
   [clojure.java.jdbc :as jdbc]
   [amble-server.db.core :as db]))

;(defn find
;  ([game-id, id]
;   (let [found
;         (jdbc/query db/db ["select * from player where gameId = ? and id = ?" game-id id])]
;     (first (map :id found)))))


(defn add!
  [game-id, player-id, id, move]
  (let [write
        (jdbc/insert! db/db :move {:id id, :gameId game-id, :playerId player-id, :move move})]
    (first (map (fn [w]
                  (let [k (first (keys w))]
                    (k w)))
                write))))

