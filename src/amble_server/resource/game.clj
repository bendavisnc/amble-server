(ns amble-server.resource.game
  (:refer-clojure :exclude [get])
  (:require
    [amble-server.db.game :as game-db]
    [taoensso.timbre :as log]))

(defn create!
  [id]
  (let [db-result (game-db/create! id)]
    (cond (game-db/failure db-result)
          (throw (ex-info "Error occured while trying to create game."
                          {:id        id
                           :db-result db-result}))
          (= {game-db/success id} db-result)
          (do
            (log/info ::create! (format "New game created, `%s`!" (name id)))
            (game-db/success db-result))

          :else (throw (ex-info
                        "An unexpected occurred while trying to create game."
                        {:id        id
                         :db-result db-result})))))

(defn get
  [id]
  (assert (keyword? id)
          (str "Bad id value provided during get, \"" id "\"."))
  (let [db-result (game-db/find id)]
    (cond
      (game-db/failure db-result)
      (throw (ex-info "Error occured while trying to get game."
                      {:id        id
                       :db-result db-result}))
      (= {game-db/success nil} db-result)
      (do (log/info ::get (format "Game not found, `%s`." (name id)))
          nil)
      (= {game-db/success id} db-result)
      (do (log/info ::get (format "Game found, `%s`." (name id)))
          (game-db/success db-result))

      :else (throw (ex-info "The unexpected occurred while trying to get game."
                            {:id        id
                             :db-result db-result})))))

(defn delete!
  [id]
  (let [db-result (game-db/delete! id)]
    (cond (game-db/failure db-result)        (throw (ex-info "Game not deleted."
                                                             {:id id
                                                              :db-result
                                                              db-result}))
          (= {game-db/success id} db-result) (do
                                               (log/info ::delete!
                                                         (format
                                                          "Game deleted, `%s`."
                                                          (name id)))
                                               id)
          :else                              (do (log/info
                                                  ::delete!
                                                  (format
                                                   "Game not deleted, `%s`."
                                                   (name id))
                                                  {:db-result db-result})
                                                 ""))))
