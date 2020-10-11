(ns amble-server.resource.game
  (:require
    [amble-server.db.game :as game-db]))

(declare inform)

(defn get [id]
  (inform "Getting" id)
  (game-db/find id))

(defn create! [id]
  (inform "Creating", id)
  (game-db/create! id))

(defn delete! [id]
  (inform "Deleting", id)
  (game-db/delete! id))

(defn inform [about, id]
  (println (str about
                " game resource by id, \""
                id
                "\".")))

