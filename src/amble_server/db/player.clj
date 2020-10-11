(ns amble-server.db.player
  (:require
    [clojure.java.jdbc :as jdbc]
    [amble-server.db.core :as db]))

(defn find [& args]
  (cond (= 2 (count args))
        (let [[game-id, id] args
              found
              (jdbc/query db/db ["select * from player where gameId = ? and id = ?" game-id id])]
          (first (map :id found)))
        (= 1 (count args))
        (let [[game-id] args
              found
              (jdbc/query db/db ["select * from player where gameId = ?" game-id])]
          (first (map :id found)))
        :default
        (throw (new Exception "Invalid arity invoking find method."))))

(defn add!
  [game-id, id]
  (let [write
        (jdbc/insert! db/db :player {:id id, :gameId game-id})]
    (first (map (fn [w]
                  (let [k (first (keys w))]
                    (k w)))
                write))))
