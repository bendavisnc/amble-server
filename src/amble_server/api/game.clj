(ns amble-server.api.game
  (:refer-clojure :exclude [get])
  (:require
    [amble-server.resource.game :as game-resource]
    [amble-server.resource.player :as player-resource]
    [amble-server.utils :as utils]
    [clojure.data.json :as json]
    [ring.util.request :as request-util]
    [ring.util.response :as response-util]
    [taoensso.timbre :as log]))


(defn add!
  "Adds a new game.
   Returns a new game id.
   Currently automatically creates six players."
  [req]
  (let [game-id-from-client (some-> req
                                    :body
                                    :game-id
                                    :keyword)]
    (try
      (let [game-id (if game-id-from-client
                      (do (log/info ::add!
                                    "Using `game-id` provided from client."
                                    {:game-id game-id-from-client})
                          game-id-from-client)
                      (let [game-id-provisioned (keyword
                                                 (utils/momentary-game-name))]
                        (log/info ::add!
                                  "Providing `game-id` to client."
                                  {:game-id game-id-provisioned})
                        game-id-provisioned))
            already-existing-game-id (game-resource/get game-id)]
        (if (not (nil? already-existing-game-id))
          (->
            (response-util/status 409))
          ; else
          (let [was-game-created (game-resource/create! game-id)
                _ (assert (some? was-game-created)
                          "Problem creating game.")
                players-created  (doall (map (fn [i]
                                               (player-resource/add!
                                                game-id
                                                (keyword (str "player-"
                                                              (nth
                                                               ["one" "two"
                                                                "three" "four"
                                                                "five" "six"]
                                                               i)))))
                                             (range 6)))
                _ (doall (map (fn [write-result]
                                (assert
                                 (not (nil? write-result))
                                 "Unexpected db result while adding game."))
                              (conj players-created was-game-created)))]

            (-> (response-util/response {:game-id game-id})
                (response-util/status 201)))))
      (catch Throwable e
        (throw (ex-info
                "An error occurred during game create."
                {:game-id-from-client :game-id-from-client}
                e))))))

(defn get-game-id
  [req]
  (let [game-id-prefix ((:headers req)
                        (name :x-amble-game-id-prefix))
        _ (when game-id-prefix
            (log/info ::get-game-id
                      (str "Using game id prefix value, \""
                           game-id-prefix
                           "\".")))
        game-id        (str game-id-prefix (utils/momentary-game-name))]
    (try
      (assert (not (empty? game-id))
              "`game-id` is nil.")
      (log/info ::get-game-id (format "Providing game id, `%s`." game-id))
      game-id
      (catch Throwable e
        (throw (ex-info
                "An error occurred during game get id."
                {}
                e))))))

(defn get
  [req]
  (try
    (let [game-id (keyword (:game-id (:params req)))]
      (if-let [game-id (game-resource/get game-id)]
        (-> (response-util/response {:game-id game-id})
            (response-util/status 200))
        (-> (response-util/response {})
            (response-util/status 404))))
    (catch Throwable e
      (throw (ex-info
              "An error occurred during game get."
              {}
              e)))))

(defn delete!
  [req]
  (try
    (let [game-id    (keyword (:game-id (:params req)))
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
      (throw (ex-info
              "An error occurred during game delete."
              {}
              e)))))
