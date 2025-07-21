(ns amble-server.db.player-sql
  (:require
   [amble-server.db.db :as db]
   [yesql.core :as yesql]))

(yesql/defquery player-add! "amble_server/db/player.sql" {:connection db/db})

(yesql/defquery player-find-by-id "amble_server/db/player.sql" {:connection db/db})

(yesql/defquery player-find-by-game-id "amble_server/db/player.sql" {:connection db/db})

(defn add!
  [game-id, id]
  (player-add! {:game_id game-id
                :id      id}))

(defn find
  ([game-id]
   (player-find-by-game-id {:game_id game-id}))
  ([game-id, id]
   (player-find-by-id {:game_id game-id
                       :id      id})))
