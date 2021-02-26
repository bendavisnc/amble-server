(ns amble-server.api.game
  (:require
   [amble-server.resource.game :as game-resource]
   [amble-server.resource.player :as player-resource]
   [ring.util.response :as response-util]
   [amble-server.utils :as utils])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.api.game"))

(defn add!
  "Adds a new game.
   Returns a new game id.
   Currently automatically creates six players."
  [req]
  (try
    (let [game-id-prefix ((:headers req)
                          (name :x-amble-game-id-prefix))
          _ (when game-id-prefix
              (.info log (str "Using game id prefix value, \""
                              game-id-prefix
                              "\".")))
          game-id (str game-id-prefix (utils/momentary-game-name))
          already-existing-game-id (game-resource/get game-id)]
      (if (not (nil? already-existing-game-id))
        (-> (response-util/response "Conflict.")
            (response-util/status 409))
        ;else
        (let [was-game-created (game-resource/create! game-id)
              _ (assert (not (nil? was-game-created))
                        "Problem creating game.")
              players-created (doall (map (fn [i]
                                            (player-resource/add! game-id (str "player-"
                                                                               (nth ["one", "two", "three", "four", "five", "six"]
                                                                                    i))))
                                          (range 6)))
              _ (doall (map (fn [write-result]
                              (assert (not (nil? write-result))
                                      "Unexpected db result while adding game."))
                            (conj players-created was-game-created)))]

          (-> (response-util/response {:game-id game-id})
              (response-util/status 201)))))
    (catch Throwable e
      (.error log "An error occurred during game create.")
      (.error log e)
      (response-util/status req 500))))

(defn get [req]
  (let [game-id (:game-id (:params req))
        found (game-resource/get game-id)]
    (try
      (cond (not found)
            (response-util/status req 404)
            :default
            (response-util/response found))
      (catch Throwable e
        (.error log "An error occurred during game get.")
        (.error log e)
        (response-util/status req 500)))))

(defn delete! [req]
  (let [game-id (:game-id (:params req))
        already-existing-game-id (game-resource/get game-id)]
    (cond (not already-existing-game-id)
          (-> (response-util/response {:game-id game-id})
              (response-util/status 200))
          :else
          (let [game-id (game-resource/delete! game-id)]
            (-> (response-util/response {:game-id game-id})
                (response-util/status 200))))))
