(ns amble-server.db.move-sql
  (:require
   [amble-server.db.core :as db]
   [amble-server.db.move-trigger :as move-trigger]
   [yesql.core :as yesql]
   [clojure.edn :as edn]
   [clojure.string :as str]
   [clojure.java.jdbc :as jdbc]
   [clojure.walk :as walk]))

(yesql/defquery move-add! "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-game-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-count "amble_server/db/move.sql" {:connection db/db})

(defn add! [game-id, player-id, player-piece-index, id, move, x, y, client-id]
  (jdbc/with-db-transaction [tx db/db]
    (.addUpdateListener (:connection tx)
                        move-trigger/listener)
    (move-add! {:game_id game-id
                :player_id player-id
                :player_piece_index player-piece-index
                :id id
                :move      move
                :x x
                :y y
                :client_id client-id} 
               {:connection tx})))

(defn find [game-id, id]
  (let [move-raw
        (first (move-find-by-id {:game_id   game-id
                                 :id        id}))

                        ;;  {:identifiers ;;#(str/replace % "_" "-")
                                      ;;  #(.replace % \_ \-)})]
        move-key-fix
        (walk/postwalk (fn [x]
                         (if-let [x-keyword (and (keyword? x)
                                             x)]
                           (keyword (str/replace  
                                                 (name x-keyword)                       
                                                 "_" 
                                                 "-")) 
                           x))
                       move-raw)
        move
        (update move-key-fix :move edn/read-string)]
    move))
    

(defn count [game-id]
  (move-count {:game_id   game-id}))
