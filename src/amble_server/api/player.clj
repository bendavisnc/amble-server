(ns amble-server.api.player
  (:require
    [amble-server.resource.game :as game-resource]
    [amble-server.resource.player :as player-resource]
    [ring.util.response :as response-util]
    [amble-server.utils :as utils]
    [clojure.java.io :as io]
    [clojure.edn :as edn]))

(defn get-all [req]
  (let [game-id (:game-id (:params req))
        game-found (game-resource/get game-id)]
    (try
      (cond (not game-found)
            (response-util/status req 404)
            :default
            (response-util/response
              (player-resource/get-all game-id)))
      (catch Throwable e
        (println "An error occurred during game board get.")
        (println e)
        (response-util/status req 500)))))

(defn get [req]
  (println "i'm confused")
  (println req)
  (let [game-id (:game-id (:params req))
        id (:player-id (:params req))
        game-found-seq (lazy-seq (game-resource/get game-id))
        player-found-seq (lazy-cat (for [_ game-found-seq]
                                     (player-resource/get game-id id)))]
    (println player-found-seq)
    player-found-seq))


(println "wut"
    (println [game-id, id])
    (try
      (cond (not game-found)
            (response-util/status req 404)
            :default
            (response-util/response
              (player-resource/get game-id id)))
      (catch Throwable e
        (println "An error occurred during game board get.")
        (println e)
        (response-util/status req 500))))

