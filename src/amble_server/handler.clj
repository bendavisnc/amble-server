(ns amble-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :as middleware-default]
            [ring.middleware.json :as middleware-json]
            [amble-server.utils :as utils]
            [amble-server.api.game :as game-api]
            [amble-server.api.player :as player-api]
            [amble-server.api.board :as board-api]))

(defn middleware-custom [handler]
  (fn [req]
    (let [response-raw (handler req)]
      ; (println response-raw)
      (assoc-in response-raw
                [:headers
                 "Access-Control-Allow-Origin"]
                "*"))))


(defroutes app-routes
           (GET "/" [] "Hello World")
           (GET "/game-id" [] (utils/momentary-game-name))
           (GET "/game/:game-id" [] game-api/get)
           (GET "/game/:game-id/board" [] board-api/get)
           (GET "/game/:game-id/player" [] player-api/get-all)
           (POST "/game" [] game-api/add!)
           (DELETE "/game/:game-id" [] game-api/delete!)
           (route/not-found "Not Found"))

(def app (middleware-json/wrap-json-response
           (middleware-custom
             (middleware-default/wrap-defaults app-routes
                                               (assoc-in middleware-default/api-defaults
                                                         [:responses, :content-types]
                                                         false)))


           {:pretty-print true}))

