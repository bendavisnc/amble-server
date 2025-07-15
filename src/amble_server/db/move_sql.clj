(ns amble-server.db.move-sql
  (:require
   [amble-server.db.core :as db]
   [amble-server.db.move-trigger :as move-trigger]
   [clojure.edn :as edn]
   [clojure.java.jdbc :as jdbc]
   [clojure.string :as str]
   [clojure.walk :as walk]
   [yesql.core :as yesql])
  (:import
   (org.apache.logging.log4j LogManager)))

(def log (. LogManager getLogger "amble-server.db.move-sql"))

(yesql/defquery move-add! "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-delete-by-id! "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-player-id "amble_server/db/move.sql" {:connection db/db})

(yesql/defquery move-find-by-rowid "amble_server/db/move.sql" {:connection db/db})

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
                :is_nullified false
                :x x
                :y y
                :client_id client-id}
               {:connection tx})))

(defn delete! [game-id, id]
  (jdbc/with-db-transaction [tx db/db]
    (.addUpdateListener (:connection tx)
                        move-trigger/listener)
    (move-delete-by-id! {:game_id (name game-id)
                         :id (name id)}
                        {:connection tx})))

(defn move-postfind [move-raw]
  (let [move-key-fix
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

(defn find [game-id, id]
  (if-let [move-raw
           (first (move-find-by-id {:game_id   game-id
                                    :id        id
                                    :is_nullified false}))]
    (move-postfind move-raw)))

(defn find-by-player-id [game-id, player-id]
  (if-let [moves-raw
           (move-find-by-player-id {:game_id (name game-id)
                                    :player_id (name player-id)
                                    :is_nullified false})]
    (map move-postfind
         moves-raw)))

(defn find-by-game-id [game-id]
  (if-let [moves-raw
           (move-find-by-game-id {:game_id (name game-id)
                                  :is_nullified false})]
    (map :id
         (map move-postfind
              moves-raw))))

(defn find-by-rowid [rowid]
  (if-let [move-raw
           (first (move-find-by-rowid {:rowid rowid}))]
    (move-postfind move-raw)))

(defn count [game-id]
  (move-count {:game_id   game-id}))
