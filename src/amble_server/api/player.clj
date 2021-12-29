(ns amble-server.api.player
  (:require
   [amble-server.resource.game :as game-resource]
   [amble-server.resource.player :as player-resource]
   [ring.util.response :as response-util]
   [amble-server.utils :as utils]
   [clojure.java.io :as io]
   [clojure.edn :as edn])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.api.player"))

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

(defn crude-player-indexes-map [player-id]
  (let [ordered
        [:player-one, :player-two, :player-three, :player-four, :player-five, :player-six]
        id-index (.indexOf ordered (keyword player-id))
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

(defn get [req]
  (let [game-id (:game-id (:params req))
        id (:player-id (:params req))
        coordinates? (= "true"
                        (:coordinates (:params req)))]
    (if-let [player-id (player-resource/get game-id, id)]
      (let [player-get {:player-id player-id}
            player-get (if coordinates?
                         (assoc player-get
                                :coordinates
                                (crude-player-indexes-map id))
                         player-get)]
        (-> (response-util/response player-get)
            (response-util/status 200)))
      (-> (response-util/response {:player-id ""})
          (response-util/status 404)))))

;(response-util/response
;(crude-player-indexes-map id)))))

