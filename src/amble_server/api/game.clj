(ns amble-server.api.game
  (:require
   [amble-server.resource.game :as game-resource]
   [amble-server.resource.player :as player-resource]
   [amble-server.utils :as utils]
   [clojure.data.json :as json]
   [ring.util.request :as request-util]
   [ring.util.response :as response-util])
  (:import
   (org.apache.logging.log4j LogManager)))

(def log (. LogManager getLogger "amble-server.api.game"))

(defn add!
  "Adds a new game.
   Returns a new game id.
   Currently automatically creates six players."
  [req]
  (try
    (let [game-id-str (request-util/body-string req)
          game-id-map (when (seq game-id-str)
                        (json/read-str game-id-str :key-fn keyword))
          game-id (some-> game-id-map :game-id keyword)
          game-id (if game-id
                    (do (.info log (format "Using `game-id` provided from client, `%s`." (name game-id)))
                        game-id)
                    (let [game-id-provisioned (keyword (utils/momentary-game-name))]
                      (.info log (format "Providing `game-id` to client, `%s`." game-id-provisioned))
                      game-id-provisioned))
          already-existing-game-id (game-resource/get game-id)]
      (if (not (nil? already-existing-game-id))
        (-> (response-util/response "Conflict.")
            (response-util/status 409))
        ; else
        (let [was-game-created (game-resource/create! game-id)
              _ (assert (not (nil? was-game-created))
                        "Problem creating game.")
              players-created (doall (map (fn [i]
                                            (player-resource/add! game-id
                                                                  (keyword (str "player-"
                                                                                (nth ["one", "two", "three", "four", "five", "six"]
                                                                                     i)))))
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

(defn get-game-id [req]
  (let [game-id-prefix ((:headers req)
                        (name :x-amble-game-id-prefix))
        _ (when game-id-prefix
            (.info log (str "Using game id prefix value, \""
                            game-id-prefix
                            "\".")))
        game-id (str game-id-prefix (utils/momentary-game-name))]
    (try
      (assert (not (empty? game-id))
              "`game-id` is nil.")
      (.info log "Providing game id.")
      (.info log (format "  \"%s\"" game-id))
      game-id
      (catch Throwable e
        (.error log "An error occurred during game get id.")
        (.error log e)
        (response-util/status req 500)))))

(defn get [req]
  (try
    (let [game-id (keyword (:game-id (:params req)))]
      (if-let [game-id (game-resource/get game-id)]
        (-> (response-util/response {:game-id game-id})
            (response-util/status 200))
        (-> (response-util/response {})
            (response-util/status 404))))
    (catch Throwable e
      (.error log "An error occurred during game get.")
      (.error log e)
      (response-util/status req 500))))

(defn delete! [req]
  (try
    (let [game-id (keyword (:game-id (:params req)))
          no-game-id ""
          already-existing-game-id (game-resource/get game-id)]
      (cond (not already-existing-game-id)
            (-> (response-util/response {:game-id no-game-id})
                (response-util/status 200))
            :else
            (let [game-id (game-resource/delete! game-id)]
              (-> (response-util/response {:game-id game-id})
                  (response-util/status 200)))))
    (catch Throwable e
      (.error log "An error occurred during game delete.")
      (.error log e)
      (response-util/status req 500))))
