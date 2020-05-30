(ns amble-server.resource.game
  (:require
    [ring.util.response :as response-util]
    [compojure.response :as response]
    [amble-server.depot.game :as game-depot])
  (:import (java.util Calendar Locale)))


(defn get-by-id [req]
  (println "Sending get game by id response.")
  (let [game-id "fortyTwo"]
    (-> (response/render game-id req)
        (response-util/status 200)
        (response-util/content-type "application/json"))))


(defn get-by-tag [tag]
  (game-depot/find-by-tag tag))


(defn day-of-week []
  (.getDisplayName (Calendar/getInstance)
                   Calendar/DAY_OF_WEEK
                   Calendar/LONG
                   (Locale/getDefault)))

(defn momentary-game-name []
  (str
      "The"
      (day-of-week)
      "Game"))

(defn create! [game-id]
  (game-depot/create! game-id))

