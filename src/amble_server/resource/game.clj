(ns amble-server.resource.game
  (:require
    [ring.util.response :as response-util]
    [compojure.response :as response]
    [amble-server.depot.game :as game-depot])
  (:import (java.util Calendar Locale)))

(defn get-by-id [id]
  (println (str "Getting game resource by id (" id ")."))
  (game-depot/find id))

;(defn search
;  [params]
;  (game-depot/find (:alias params)))

(defn create! [game-id]
  (println (str "Creating new game resource for id, "
                game-id
                "."))
  (game-depot/create! game-id))

(defn delete! [game-id]
  (println "Getting game resource by id.")
  (game-depot/delete! game-id))

