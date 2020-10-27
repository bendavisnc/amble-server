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
        f (game-resource/get game-id)
        g (lazy-seq f)
        game-found-seq (vec g)
        _ (assert (= 1 (count game-found-seq)))
        player-found-seq (vec (lazy-cat (for [_ game-found-seq]
                                          (player-resource/get game-id id))))]
    (println "wellllllllllllll")
    (doall player-found-seq)
    (println "cooooool?")
    (println player-found-seq)
    (println "no?")
    (println game-found-seq)
    player-found-seq))


;(println "wut"
;    (println [game-id, id])
;    (try
;      (cond (not game-found)
;            (response-util/status req 404)
;            :default
;            (response-util/response
;              (player-resource/get game-id id)))
;      (catch Throwable e
;        (println "An error occurred during game board get.")
;        (println e)
;        (response-util/status req 500))))
;
