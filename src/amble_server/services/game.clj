(ns amble-server.services.game
  (:require
    [ring.util.response :as response-util]
    [compojure.response :as response]))

(defn get-by-id [req]
  (println "Sending get game by id response.")
  (let [game-id "fortyTwo"
        response-raw
        (response-util/status (response/render game-id req)
                              200)]

    (println response-raw)
    (assoc-in response-raw
              [:headers
               "Access-Control-Allow-Origin"]
              "*")))
      

(defn create [req]
  (let [game-id "immaPretendGameId"]
    (response-util/status (response/render game-id req)
                          201)))