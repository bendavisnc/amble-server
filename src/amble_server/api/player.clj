(ns amble-server.api.player
  (:refer-clojure :exclude [get])
  (:require
    [amble-server.resource.game :as game-resource]
    [amble-server.resource.move :as move-resource]
    [amble-server.resource.player :as player-resource]
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [ring.util.response :as response-util]
    [taoensso.timbre :as log]))


(defn get-all
  [req]
  (let [game-id (some-> req
                        :params
                        :game-id
                        keyword)]
    (try (let [game-found (game-resource/get game-id)]
           (cond (not game-found) (response-util/status req 404)
                 :else            (response-util/response
                                   (player-resource/get-all game-id))))
         (catch Throwable e
           (throw (ex-info
                   "An error occurred during game player get all."
                   {:game-id game-id}
                   e))))))



(defn crude-player-indexes-map
  [player-id]
  (let [ordered
        [:player-one :player-two :player-three :player-four :player-five
         :player-six]
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
        board-coords-resource (io/resource "board.json")
        _ (when-not board-coords-resource
            (throw (new Exception "Could not find board.json resource.")))
        boord-coords
        (edn/read-string (slurp board-coords-resource))
        player-coords (vec (map (fn [i]
                                  (nth boord-coords i))
                                player-coord-indexes))]
    player-coords))

(defn merge-positions-and-moves
  [simple-position-list moves-existing]
  (reduce (fn [acc move]
            (assoc acc
                   (Integer/parseInt (:player-piece-index move)) ;; maybe
                                                                 ;; revisit
                   [(:x move)
                    (:y move)]))
          simple-position-list
          moves-existing))

(defn get
  "Returns either an existing player's current position as a list of two value vectors, or a not found response, or an error response."
  [req]
  (let [game-id   (some-> req
                          :params
                          :game-id
                          keyword)
        player-id (some-> req
                          :params
                          :player-id
                          keyword)]
    (try (let [

               player-existing (player-resource/get game-id player-id)
               player-simple-position-list (crude-player-indexes-map player-id)]
           (cond
             (nil? player-existing)
             (do
               (log/info ::get
                         "No player found for game."
                         {:game-id   game-id
                          :player-id player-id})
               (-> (response-util/response [])
                   (response-util/status 404)))
             (empty? player-simple-position-list)
             (do (log/info ::get
                           "Returning existing player with no moves existing."
                           {:game-id   game-id
                            :player-id player-id})
                 (-> (response-util/response (crude-player-indexes-map
                                              player-id))
                     (response-util/status 200)))
             :else
             (let [moves-existing
                   (move-resource/get-by-player-id game-id
                                                   player-id)]
               (-> (response-util/response (merge-positions-and-moves
                                            player-simple-position-list
                                            moves-existing))
                   (response-util/status 200)))))

         (catch Throwable e
           (throw
            (ex-info
             "Something bad happened when trying to get a player from the game."
             {:game-id   game-id
              :player-id player-id}
             e))))))
