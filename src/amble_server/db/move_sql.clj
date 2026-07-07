(ns amble-server.db.move-sql
  (:refer-clojure :exclude [find count])
  (:require
    [amble-server.config :as amble-config]
    [amble-server.db.db :as db]
    [amble-server.db.move-trigger.sqlite :as move-trigger-sqlite]
    [clojure.edn :as edn]
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as str]
    [clojure.walk :as walk]
    [yesql.core :as yesql]))

#_:clj-kondo/ignore
(yesql/defquery move-add! "amble_server/db/move.sql" {:connection db/db})

#_:clj-kondo/ignore
(yesql/defquery move-delete-by-id!
                "amble_server/db/move.sql"
                {:connection db/db})

#_:clj-kondo/ignore
(yesql/defquery move-find-by-id "amble_server/db/move.sql" {:connection db/db})

#_:clj-kondo/ignore
(yesql/defquery move-find-by-player-id
                "amble_server/db/move.sql"
                {:connection db/db})

#_:clj-kondo/ignore
(yesql/defquery move-find-by-rowid
                "amble_server/db/move.sql"
                {:connection db/db})

#_:clj-kondo/ignore
(yesql/defquery move-find-by-game-id
                "amble_server/db/move.sql"
                {:connection db/db})

#_:clj-kondo/ignore
(yesql/defquery move-count "amble_server/db/move.sql" {:connection db/db})

(defn add!
  [game-id player-id player-piece-index id move x y client-id]
  (jdbc/with-db-transaction [tx db/db]
                            (when-not amble-config/postgres?
                              (.addUpdateListener (:connection tx)
                                                  move-trigger-sqlite/listener))
                            (move-add! {:game_id      game-id
                                        :player_id    player-id
                                        :player_piece_index player-piece-index
                                        :id           id
                                        :move         move
                                        :is_nullified false
                                        :x            x
                                        :y            y
                                        :client_id    client-id}
                                       {:connection tx})))

(defn delete!
  [game-id id]
  (jdbc/with-db-transaction [tx db/db]
                            (when-not amble-config/postgres?
                              (.addUpdateListener (:connection tx)
                                                  move-trigger-sqlite/listener))
                            (move-delete-by-id! {:game_id (name game-id)
                                                 :id      (name id)}
                                                {:connection tx})))

(defn move-postfind
  [move-raw]
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

(defn find
  [game-id id]
  (assert (string? game-id)
          (format "`game-id` must be a string, but was %s." (type game-id)))
  (assert (string? id)
          (format "`id` must be a string, but was %s." (type id)))
  (when-let [move-raw
             (first (move-find-by-id {:game_id      game-id
                                      :id           id
                                      :is_nullified false}))]
    (move-postfind move-raw)))

(defn find-by-player-id
  [game-id player-id]
  (when-let [moves-raw
             (move-find-by-player-id {:game_id      (name game-id)
                                      :player_id    (name player-id)
                                      :is_nullified false})]
    (map move-postfind
         moves-raw)))

(defn find-by-game-id
  [game-id]
  (when-let [moves-raw
             (move-find-by-game-id {:game_id      (name game-id)
                                    :is_nullified false})]
    (map :id
         (map move-postfind
              moves-raw))))

(defn find-by-rowid
  [rowid]
  (assert (instance? Long rowid)
          (format "Rowid must be a long, but was %s." (type rowid)))
  (when-let [move-raw
             (first (move-find-by-rowid {:rowid rowid}))]
    (move-postfind move-raw)))

(defn count
  [game-id]
  (move-count {:game_id game-id}))
