(ns amble-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :as middleware-default]
            [ring.middleware.json :as middleware-json]
            [ring.util.response :as response-util]
            [compojure.response :as response]
            [amble-server.resource.game :as game]
            [amble-server.utils :as utils]))

(defn middleware-custom [handler]
  (fn [req]
    (let [response-raw (handler req)]
      ; (println response-raw)
      (assoc-in response-raw
                [:headers
                 "Access-Control-Allow-Origin"]
                "*"))))

(defn game-get-by-tag [req]
  (let [tag (:tag (:params req))
        found (game/get-by-tag tag)]
    (cond (not found)
          (response-util/status req
                                404)
          :default
          (response-util/response
            {:game-id found}))))

(defn game-create! [_]
  (let [game-id (utils/momentary-game-name)
        already-existing-game-id (game/get-by-tag game-id)]
    (if (not (nil? already-existing-game-id))
      (-> (response-util/response "Conflict.")
          (response-util/status 409))
      ;else
      (let [
            game-id (game/create! game-id)]
        (assert (= (type "")
                   (type game-id))
                "Expected game id to be a string.")
        (-> (response-util/response {:game-id game-id})
            (response-util/status 201))))))

(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/game/:game-id" [] game/get-by-id)
           (GET "/game" [] game-get-by-tag)
           (POST "/game" [] game-create!)
           (route/not-found "Not Found"))

(def app (middleware-json/wrap-json-response
           (middleware-custom
              (middleware-default/wrap-defaults app-routes
                                                  (assoc-in middleware-default/api-defaults
                                                              [:responses, :content-types]
                                                              false)))


           {:pretty-print true}))




