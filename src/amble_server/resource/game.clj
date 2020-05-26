(ns amble-server.services.game
  (:require
    [ring.util.response :as response-util]
    [compojure.response :as response]))


(defn get-by-id [req]
  (println "Sending get game by id response.")
  (let [game-id "fortyTwo"]
    (-> (response/render game-id req)
        (response-util/status 200)
        (response-util/content-type "application/json"))))


(defn get-by-tag [req]
  (println "Sending get game by tag response.")
  (let [games-found ["immaPretendGameBranchName"]]
    (-> (response/render (str games-found) req)
        (response-util/status 200)
        (response-util/content-type "application/json"))))


(defn create [req]
  (let [game-id "immaPretendGameId"]
    (response-util/status (response/render game-id req)
                          201)))