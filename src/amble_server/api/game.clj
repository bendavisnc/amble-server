(ns amble-server.api.game
  (:require
    [amble-server.resource.game :as game-resource]
    [ring.util.response :as response-util]
    [amble-server.utils :as utils]))



(defn add! [req]
  (let [game-id-prefix ((:headers req)
                        (name :x-amble-game-id-prefix))
        _ (when game-id-prefix
            (println (str "Using game id prefix value, \""
                          game-id-prefix
                          "\".")))
        game-id (str game-id-prefix (utils/momentary-game-name))
        already-existing-game-id (game-resource/get game-id)]
    (if (not (nil? already-existing-game-id))
      (-> (response-util/response "Conflict.")
          (response-util/status 409))
      ;else
      (let [
            game-id (game-resource/create! game-id)]
        (assert (= (type "")
                   (type game-id))
                "Expected game id to be a string.")
        (-> (response-util/response {:game-id game-id})
            (response-util/status 201))))))

(defn get [req]
  (let [game-id (:game-id (:params req))
        found (game-resource/get game-id)]
    (cond (not found)
          (response-util/status req
                                404)
          :default
          (response-util/response found))))


(defn delete! [req]
  (let [game-id (:game-id (:params req))
        already-existing-game-id (game-resource/get game-id)]
    (cond (not already-existing-game-id)
          (-> (response-util/response {:game-id game-id})
              (response-util/status 200))
          :else
          (let [
                game-id (game-resource/delete! game-id)]
            (-> (response-util/response {:game-id game-id})
                (response-util/status 200))))))
