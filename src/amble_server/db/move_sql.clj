(ns amble-server.db.move-sql
  (:require
   [amble-server.db.core :as db]
   [yesql.core :as yesql]
   [clojure.string :as str]))

(yesql/defquery move-add! "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-game-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-count "amble_server/db/move.sql" {:connection db/db})

(defn add! [game-id, player-id, player-piece-index, id, move, x, y]
   (println "wut wut")
   (println
            {:game_id game-id
             :player_id player-id
             :player_piece_index player-piece-index
             :id id
             :move      move
             :x x
             :y y})
   (move-add! {:game_id game-id
               :player_id player-id
               :player_piece_index player-piece-index
               :id id
               :move      move
               :x x
               :y y}))

(defn find [game-id, id]
  (move-find-by-id {:game_id   game-id
                    :id        id}
                   {:identifiers #(str/replace % "_" "-")}))

(defn count [game-id]
  (move-count {:game_id   game-id}
              {:identifiers #(str/replace % "_" "-")}))



  ;; (let [[x, y] (last move)
        ;; game-move-index (move-count {:game_id game-id})))
 
