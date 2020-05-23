(ns amble-server.services.game
  (:require
    [ring.util.response :as response-util]
    [compojure.response :as response]))

(defn create [req]
  (let [gameId "immaPretendGameId"]
    (response-util/status (response/render gameId req)
                          201)))