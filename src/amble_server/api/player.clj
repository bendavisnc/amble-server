(ns amble-server.api.player
  (:refer-clojure :exclude [get])
  (:require
    [amble-server.resource.game :as game-resource]
    [amble-server.resource.move :as move-resource]
    [amble-server.resource.player :as player-resource]
    [clojure.data.json :as json]
    [clojure.java.io :as io]
    [ring.util.response :as response-util]
    [taoensso.timbre :as log]))

(defmacro data-from-json
  [filename]
  (let [resource (io/resource filename)
        _ (assert resource (format "Could not find `%s` resource." filename))
        v        (json/read-str (slurp resource) :key-fn keyword)]
    v))

(def board (data-from-json "board.json"))

(def player-initial-placements
  (let [player-indexes (data-from-json "board-players.json")]
    (into {}
          (for [[player-id indexes] player-indexes
                :let [positions (vec (map board indexes))]]
            [player-id positions]))))

(defn get-all
  [req]
  (let [game-id (some-> req
                        :params
                        :game-id
                        keyword)]
    (try (let [game-found (game-resource/get game-id)]
           (if (not game-found)
             (response-util/status req 404)
             (response-util/response
              (player-resource/get-all game-id))))
         (catch Throwable e
           (throw (ex-info
                   "An error occurred during game player get all."
                   {:game-id game-id}
                   e))))))


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
    (try
      (let [player-existing (player-resource/get game-id player-id)]
        (if
          (nil? player-existing)
          (do
            (log/info ::get
                      "No player found for game."
                      {:game-id   game-id
                       :player-id player-id})
            (-> (response-util/response [])
                (response-util/status 404)))
          ;; else
          (let [moves-existing (move-resource/get-by-player-id game-id
                                                               player-id)
                positions      (reduce
                                (fn [acc {:keys [x y :player-piece-index]}]
                                  (assoc acc player-piece-index [x y]))
                                (or (player-initial-placements player-id)
                                    (throw (ex-info "Bad `player-id`."
                                                    {:player-id player-id})))
                                moves-existing)]
            (-> (response-util/response positions)
                (response-util/status 200)))))

      (catch Throwable e
        (throw
         (ex-info
          "Something bad happened when trying to get a player from the
             game."
          {:game-id   game-id
           :player-id player-id}
          e))))))
