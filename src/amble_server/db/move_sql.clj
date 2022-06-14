(ns amble-server.db.move-sql
  (:require
   [amble-server.db.core :as db]
   [amble-server.db.move-trigger :as move-trigger]
   [yesql.core :as yesql]
   [clojure.string :as str]
   [clojure.java.jdbc :as jdbc]))

(yesql/defquery move-add! "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-game-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-count "amble_server/db/move.sql" {:connection db/db})

(defn add! [game-id, player-id, player-piece-index, id, move, x, y]
  (jdbc/with-db-transaction [tx db/db]
    (.addUpdateListener (:connection tx)
                        move-trigger/listener)
    (move-add! {:game_id game-id
                :player_id player-id
                :player_piece_index player-piece-index
                :id id
                :move      move
                :x x
                :y y}
               {:connection tx})))

(defn find [game-id, id]
  (move-find-by-id {:game_id   game-id
                    :id        id}
                   {:identifiers #(str/replace % "_" "-")}))

(defn count [game-id]
  (move-count {:game_id   game-id}
              {:identifiers #(str/replace % "_" "-")}))



  ;; (let [[x, y] (last move)
        ;; game-move-index (move-count {:game_id game-id})))
 
