(ns amble-server.db.game-sql
  (:refer-clojure :exclude [find])
  (:require
    [amble-server.db.db :as db]
    [yesql.core :as yesql]))

#_:clj-kondo/ignore
(yesql/defquery game-add! "amble_server/db/game.sql" {:connection db/db})

#_:clj-kondo/ignore
(yesql/defquery game-find-by-id "amble_server/db/game.sql" {:connection db/db})

#_:clj-kondo/ignore
(yesql/defquery game-delete! "amble_server/db/game.sql" {:connection db/db})

(defn create!
  [id]
  (game-add! {:id id}))

(defn find
  [id]
  (game-find-by-id {:id id}))

(defn delete!
  [id]
  (game-delete! {:id id}))
