(ns amble-server.resource.game
  (:require
    [amble-server.depot.game :as game-depot]))

(declare inform)

(defn get [id]
  (inform "Getting" id)
  (let [
        designatee-coords (game-depot/find id)
        piece-indexes [
                       [120,119,118,117,116,115,114,113,112,111]
                       [95,84,97,74,86,65,76,88,99,101],
                       [10,11,20,12,22,33,13,24,35,45]
                       [0,1,2,3,4,5,6,7,8,9],
                       [25,36,23,21,34,46, 19,32,44,55]
                       [110,109,100,108,98,87,107,75,85,96,107]]]



    (and designatee-coords
      {:designatee-coords designatee-coords
       :piece-indexes piece-indexes})))

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

