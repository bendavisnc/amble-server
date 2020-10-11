(ns amble-server.resource.game
  (:require
    [amble-server.db.game :as game-db]))

(declare inform)

(defn get [id]
  (inform "Getting" id)
  (let [
        designatee-coords (game-db/find id)
        piece-indexes
                      [[111 112 113 114 115 116 117 118 119 120]
                       [65 74 76 84 86 88 95 97 99 101]
                       [10 11 12 13 20 22 24 33 35 45]
                       [0 1 2 3 4 5 6 7 8 9]
                       [19 21 23 25 32 34 36 44 46 55]
                       [75 85 87 96 98 100 107 108 109 110]]]



    (and designatee-coords
      {:designatee-coords designatee-coords
       :piece-indexes piece-indexes})))

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

