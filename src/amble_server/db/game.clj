(ns amble-server.db.game
  (:require
   [clojure.java.jdbc :as jdbc]
   [amble-server.db.core :as db]))

(defn find [id]
  (let [id (if (keyword? id)
             (name id)
             id)
        found
        (jdbc/query db/db ["select * from game where id = ?" id])]
    (first (map :id found))))

(defn create!
  [id]
  (let [write
        (jdbc/insert! db/db :game {:id id})]
    (first (map (fn [w]
                  (let [k (first (keys w))]
                    (k w)))
                write))))

;; todo
(defn delete!
  "Deletes the branch with the given game id."
  [game-id]
  nil)
