(ns amble-server.db.move-sql
  (:require
   [amble-server.db.core :as db]
   [yesql.core :as yesql]
   [clojure.string :as str]))

(yesql/defquery move-add! "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-game-id "amble_server/db/move.sql" {:connection db/db})

(defn add!
  [game-id, player-id, id, move]
  (move-add! {:game_id game-id
              :player_id player-id
              :id id
              :move move}))

(defn find [game-id, player-id, id]
  (move-find-by-id {:game_id game-id
                    :player_id player-id
                    :id id}
                   {:identifiers #(str/replace % "_" "-")}))
