(ns amble-server.api.player
  (:require
   [amble-server.resource.game :as game-resource]
   [amble-server.resource.player :as player-resource]
   [amble-server.resource.move :as move-resource]
   [ring.util.response :as response-util]
   [amble-server.utils :as utils]
   [clojure.java.io :as io]
   [clojure.edn :as edn])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.api.player"))

(defn get-all [req]
  (let [game-id (keyword (:game-id (:params req)))
        game-found (game-resource/get game-id)]
    (try
      (cond (not game-found)
            (response-util/status req 404)
            :else 
            (response-util/response
             (player-resource/get-all game-id)))
      (catch Throwable e
        (.error log "An error occurred during game board get.")
        (.error log e)
        (response-util/status req 500)))))

(defn crude-player-indexes-map [player-id]
  (let [ordered
        [:player-one, :player-two, :player-three, :player-four, :player-five, :player-six]
        id-index (.indexOf ordered player-id)
        _ (assert (not (neg-int? id-index)))
        index-list
        [[111 112 113 114 115 116 117 118 119 120]
         [65 74 76 84 86 88 95 97 99 101]
         [10 11 12 13 20 22 24 33 35 45]
         [0 1 2 3 4 5 6 7 8 9]
         [19 21 23 25 32 34 36 44 46 55]
         [75 85 87 96 98 100 107 108 109 110]]
        player-coord-indexes (index-list id-index)
        boord-coords
        (edn/read-string (slurp (io/resource "board.json")))
        player-coords (vec (map (fn [i]
                                  (nth boord-coords i))
                                player-coord-indexes))]
    player-coords))

;; (defn get [req]
;;   (let [game-id (:game-id (:params req))
;;         id (:player-id (:params req))]
;;     (if-let [_ (player-resource/get game-id, id)]
;;       (-> (response-util/response (crude-player-indexes-map id))
;;           (response-util/status 200))
;;       (-> (response-util/response [])
;;           (response-util/status 404)))))

(defn merge-positions-and-moves [simple-position-list, moves-existing]
  (.info log "whats going on?")
  (.info log (vec moves-existing))
  (.info log simple-position-list) 
  (reduce (fn [acc, move]
            (assoc acc 
                   (Integer/parseInt (:player-piece-index move)) ;; maybe revisit
                   [(:x move)
                    (:y move)]))
          simple-position-list
          moves-existing))

(defn get
  "Returns either an existing player's current position as a list of two value vectors, or a not found response, or an error response."
  [req]
  (try
    (let [game-id (keyword (:game-id (:params req)))
          player-id (keyword (:player-id (:params req)))
          _ (.info log (str "wtffffff " (type player-id)))
          player-existing (player-resource/get game-id, player-id)
          _ (.info log (str "wtf " player-existing))
          player-simple-position-list (crude-player-indexes-map player-id)]
      (cond 
            (nil? player-existing)
            (do (.info log "No player found for game, \"" game-id "\".")
                (-> (response-util/response [])
                    (response-util/status 404)))

            (empty? player-simple-position-list)
            (do (.info log "Returning existing player with no moves existing.")
                (-> (response-util/response (crude-player-indexes-map player-id))
                    (response-util/status 200)))

            true
            (let [moves-existing
                  (move-resource/get-by-player-id game-id
                                                  player-id)]
              (-> (response-util/response (merge-positions-and-moves player-simple-position-list moves-existing)) 
                  (response-util/status 200)))))


    (catch Throwable e
      (.error log "Something bad happened when trying to get a player from the game," "\"" (:game-id (:params req)) "\".")
      (.error log e)
      (response-util/status req 500))))


;(response-util/response
;(crude-player-indexes-map id)))))

