(ns amble-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :as middleware-default]
            [ring.middleware.json :as middleware-json]
            [amble-server.utils :as utils]
            [amble-server.api.game :as game-api]
            [amble-server.api.player :as player-api]
            [amble-server.api.move :as move-api]
            [amble-server.api.board :as board-api]))

(defn middleware-custom [handler]
  (fn [req]
    (let [response-raw (handler req)]
      ; (println response-raw)
      (-> response-raw
          (assoc-in [:headers
                     "Access-Control-Allow-Origin"]
                    "*")
          (assoc-in [:headers
                     "Access-Control-Allow-Methods"]
                    "*")
          (assoc-in [:headers
                     "Access-Control-Allow-Headers"]
                    "*")))))

(defroutes app-routes
           (OPTIONS "*" [] "")
           (GET "/" [] "Hello World")
           (GET "/id/default-game" [] game-api/get-game-id)
           (GET "/game/:game-id" [] game-api/get)
           (GET "/game/:game-id/board" [] board-api/get)
           (GET "/game/:game-id/player" [] player-api/get-all)
           (GET "/game/:game-id/player/:player-id" [] player-api/get)
           (POST "/game" [] game-api/add!)
           (POST "/game/:game-id/player/:player-id/move/:player-piece-index" [] move-api/add!)
           (GET "/game/:game-id/player/:player-id/move/:id" [] move-api/get)
           (OPTIONS "/game/:game-id/player/:player-id/move" [] "")
           (DELETE "/game/:game-id" [] game-api/delete!)
           (route/not-found "Not Found"))

(def app (middleware-json/wrap-json-response
           (middleware-custom
              (middleware-default/wrap-defaults app-routes
                                                  (assoc-in middleware-default/api-defaults
                                                              [:responses, :content-types]
                                                              false)))

           {:pretty-print true}))

