(ns amble-server.api.move
  (:require
   [amble-server.resource.game :as game-resource]
   [amble-server.resource.move :as move-resource]
   [clojure.data.json :as json]
   [ring.util.request :as request-util]
   ;; [amble-server.async-resource.move :as async-resource-move]
   [ring.util.response :as response-util])
  (:import
   (org.apache.logging.log4j LogManager Logger)))

(def log (. LogManager getLogger "amble-server.api.move"))

(defn get
  "Returns either an existing move, or a not found response, or an error response."
  [req]
  (try
    (let [game-id (:game-id (:params req))
          id (:id (:params req))]
      (if-let [move-existing (move-resource/get game-id, id)]
        (-> (response-util/response move-existing)
            (response-util/status 200))
        (-> (response-util/response [])
            (response-util/status 404))))
    (catch Throwable e
      (.error log "Something bad happened when trying to add a move to the game," "\"" (:game-id (:params req)) "\".")
      (.error log e)
      (response-util/status req 500))))

(defn get-all [req]
  (let [game-id (keyword (:game-id (:params req)))
        game-found (game-resource/get game-id)]
    (try
      (cond (not game-found)
            (response-util/status req 404)
            :else
            (response-util/response
              (move-resource/get-all game-id)))
      (catch Throwable e
        (.error log "An error occurred during game move get all.")
        (.error log e)
        (response-util/status req 500)))))

(defn add!
  "Returns a new move or an error response."
  [req]
  (try
    (let [game-id (:game-id (:params req))
          player-id (:player-id (:params req))
          player-piece-index (:player-piece-index (:params req))
          {:keys [move, x, y, client-id]} (json/read-str (request-util/body-string req)
                                                         :key-fn keyword)
          add (move-resource/add! game-id, player-id, player-piece-index, move, x, y, client-id)
          move-response-value (move-resource/get game-id, add)]
      (-> (response-util/response move-response-value)
          (response-util/status 201)))
    (catch Throwable e
      (.error log "Something bad happened when trying to add a move to the game," "\"" (:game-id (:params req)) "\".")
      (.error log e)
      (response-util/status req 500))))

(defn delete!
  "Deletes a move or returns an error response."
  [req]
  (try
    (let [game-id (:game-id (:params req))
          id (:id (:params req))
          _ (move-resource/delete! game-id, id)]
      (response-util/response nil))
    (catch Throwable e
      (.error log "Something bad happened when trying to delete a move from the game," "\"" (:game-id (:params req)) "\".")
      (.error log e)
      (response-util/status req 500))))
