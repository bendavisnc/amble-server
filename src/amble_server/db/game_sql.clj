(ns amble-server.db.game-sql
  (:require
    [yesql.core :as yesql]
    [amble-server.db.core :as db]))

(yesql/defquery game-add! "amble_server/db/game.sql" {:connection db/db})

(yesql/defquery game-find-by-id "amble_server/db/game.sql" {:connection db/db})


(yesql/defquery game-delete! "amble_server/db/game.sql" {:connection db/db})

(defn create!
  [id]
  (game-add! {:id id}))

(defn find [id]
  (let [a
        (game-find-by-id {:id id})]
    (println "hello")
    (println a)
    a))

(defn delete!
  [id]
  (game-delete! {:id id}))
