(ns amble-server.api.move
  (:require
    [amble-server.resource.move :as move-resource]
    [amble-server.resource.game :as game-resource]
    ;; [amble-server.async-resource.move :as async-resource-move]
    [ring.util.response :as response-util]
    [ring.util.request :as request-util]
    [clojure.data.json :as json])
  (:import [java.util Base64]
           [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(declare move-id)

(def log (. LogManager getLogger "amble-server.api.move"))

(defn add!
  "Returns a new move or an error response."
  [req]
  (try
    (let [game-id (:game-id (:params req))
          player-id (:player-id (:params req))
          player-piece-index (:player-piece-index (:params req))
          {:keys [move]} (json/read-str (request-util/body-string req)
                                        :key-fn keyword)
          add (move-resource/add! game-id, player-id, player-piece-index, move)
          move-response-value (move-resource/get game-id, add)]
      (-> (response-util/response move-response-value) 
          (response-util/status 201)))
    (catch Throwable e
      (.error log "Something bad happened when trying to add a move to the game," "\"" (:game-id (:params req)) "\".")
      (.error log e)
      (response-util/status req 500))))

(defn get 
  "Returns an existing move or an error response."
  [req]
  (try
    (let [game-id (:game-id (:params req))
          id (:id (:params req))
          find (move-resource/get game-id, id)]
      (-> (response-util/response find) 
          (response-util/status 200)))
    (catch Throwable e
      (.error log "An error occurred during move get.")
      (.error log e)
      (response-util/status req 500))))
