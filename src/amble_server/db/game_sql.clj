(ns amble-server.db.game-sql
  (:refer-clojure :exclude [find])
  (:require
    [amble-server.db.db :as db]
    [yesql.core :as yesql]))

(yesql/defquery game-add! "amble_server/db/game.sql" {:connection db/db})

(yesql/defquery game-find-by-id "amble_server/db/game.sql" {:connection db/db})

(yesql/defquery game-delete! "amble_server/db/game.sql" {:connection db/db})

(defn create!
  [id]
  (game-add! {:id id}))

(defn find
  [id]
  (let [a
        (game-find-by-id {:id id})]
    a))

(defn delete!
  [id]
  (game-delete! {:id id}))
