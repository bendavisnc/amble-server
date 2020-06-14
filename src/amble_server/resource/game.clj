(ns amble-server.resource.game
  (:require
    [amble-server.depot.game :as game-depot]))

(declare inform)

(defn get [id]
  (inform "Getting" id)
  (game-depot/find id))

;(defn search
;  [params]
;  (game-depot/find (:alias params)))

(defn create! [id]
  (inform "Creating", id)
  (game-depot/create! id))

(defn delete! [id]
  (inform "Deleting", id)
  (game-depot/delete! id))

(defn inform [about, id]
  (println (str about
                " game resource by id, \""
                id
                "\".")))

