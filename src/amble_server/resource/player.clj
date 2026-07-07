(ns amble-server.resource.player
  (:refer-clojure :exclude [get])
  (:require
    [amble-server.db.game :as game-db]
    [amble-server.db.player :as player-db]
    [taoensso.timbre :as log]))

(defn add!
  [game-id player-id]
  (first
   (for
     [game-db-find   [(game-db/find game-id)]
      :when          (or (= {game-db/success game-id} game-db-find)
                         (do
                           (log/info "No game found by id to add player."
                                     {:game-id game-id :player-id player-id})
                           nil))
      player-db-find [(player-db/find game-id player-id)]
      :when          (or (= {player-db/success nil} player-db-find)
                         (do
                           (log/info "Cannot add player that already exists."
                                     {:game-id game-id :player-id player-id})
                           nil))
      player-db-add  [(player-db/add! game-id player-id)]
      :when
      (or (and (player-db/failure player-db-add)
               (throw
                (ex-info
                 "Error occured while trying to add player to game."
                 {:game-id   game-id
                  :player-id player-id
                  :db-result player-db-add})))
          true)
      :when
      (or
       (and
        (not (= {player-db/success player-id} player-db-add))
        (throw
         (ex-info
          "An unexpected error occurred while trying to add player to game."
          {:game-id   game-id
           :player-id player-id
           :db-result player-db-add})))
       true)]
     player-id)))

(defn get-all
  [game-id]
  (first
   (for [game-db-find   [(game-db/find game-id)]
         :when          (or (= {game-db/success game-id} game-db-find)
                            (do
                              (log/info ::get-all
                                        "No game found."
                                        {:game-id game-id})
                              nil))
         player-db-find [(player-db/find game-id)]
         :when          (or (and (player-db/failure player-db-find)
                                 (throw
                                  (ex-info
                                   "Problem while getting players of game."
                                   {:game-id   game-id
                                    :db-result player-db-find})))
                            true)]
     (player-db/success player-db-find))))

(defn get
  [game-id player-id]
  (first
   (for [game-db-find   [(game-db/find game-id)]
         :when          (or (= {game-db/success game-id} game-db-find)
                            (do
                              (log/info ::get
                                        "No game found for player."
                                        {:game-id   game-id
                                         :player-id player-id})
                              nil))
         player-db-find [(player-db/find game-id player-id)]
         :when          (or (= {player-db/success player-id} player-db-find)
                            (log/info ::get
                                      "No player found for game."
                                      {:game-id   game-id
                                       :player-id player-id})
                            nil)
        ]
     player-id)))
