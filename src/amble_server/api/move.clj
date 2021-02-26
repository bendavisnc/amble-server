(ns amble-server.api.move
  (:require
   [amble-server.resource.move :as move-resource]
   [amble-server.resource.game :as game-resource]
   [amble-server.async-resource.move :as async-resource-move]
   [ring.util.response :as response-util]
   [ring.util.request :as request-util]
   [clojure.data.json :as json])
  (:import [java.util Base64]
           [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(declare move-id)

(def log (. LogManager getLogger "amble-server.api.move"))

(defn add!
  "Returns either a not found, an error, or a successful new move's id."
  [req]
  (try
    (let [game-id (:game-id (:params req))
          player-id (:player-id (:params req))
          move (json/read-str (request-util/body-string req)
                              :key-fn keyword)
          _ (.debug log "move")
          _ (.debug log move)
          game-found (game-resource/get game-id)]
      (cond (not game-found)
            (response-util/status req 404)

            (or (not game-id)
                (not player-id))
            (-> (response-util/response "Missing parameters. \n  \"gameId\" and \"moveId\" are both required.")
                (response-util/status 400))
            :default
            (let [move-id (move-id player-id)
                  _ (assert (= move-id
                               (move-resource/add! game-id, player-id, move-id, move))
                            (format "Problem with adding player's, \"%s\", move, \"%s\".", player-id, move-id))]
              (async-resource-move/post-announcement! game-id, player-id, move-id)
              (-> (response-util/response {:move-id move-id})
                  (response-util/status 201)))))
    (catch Throwable e
      (.error log "An error occurred during game move add.")
      (.error log e)
      (response-util/status req 500))))

(defn move-id [player-id]
  (apply str
         (.encode (Base64/getEncoder)
                  (.getBytes (str [player-id, (System/currentTimeMillis)])))))
