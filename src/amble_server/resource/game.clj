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


(defn get-by-tag [req]
  (println "Sending get game by tag response.")
  (let [tag (:tag (:params req))
        found (game-depot/find-by-tag tag)]
    (cond (not found)
          (response-util/status req
                                404)
          :default
          (do
            (println found)
            (response-util/status (response/render found req)
                                  200)))))

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

(defn create [req]
  (let [game-id (game-depot/create (momentary-game-name))]
    (response-util/status (response/render game-id req)
                          201)))
