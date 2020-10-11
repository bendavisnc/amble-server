(ns amble-server.api.board
  (:require
    [amble-server.resource.game :as game-resource]
    [ring.util.response :as response-util]
    [amble-server.utils :as utils]
    [clojure.java.io :as io]))

(defn get [req]
  (let [game-id (:game-id (:params req))
        found (game-resource/get game-id)]
    (try
      (cond (not found)
            (response-util/status req 404)
            :default
            ;(response-util/response (slurp (io/resource "board.json")))
            (response-util/resource-response "board.json"))
      (catch Throwable e
        (println "An error occurred during game board get.")
        (println e)
        (response-util/status req 500)))))

