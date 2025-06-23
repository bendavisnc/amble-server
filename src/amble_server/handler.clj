(ns amble-server.handler
  (:require
   [amble-server.api.board :as board-api]
   [amble-server.api.game :as game-api]
   [amble-server.api.move :as move-api]
   [amble-server.api.player :as player-api]
   [amble-server.config :as amble-config]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.defaults :as middleware-default]
   [ring.middleware.json :as middleware-json]))

(defn wrap-cors-for-client [handler]
  (wrap-cors handler
    :access-control-allow-origin [(re-pattern amble-config/client-url)]
    :access-control-allow-methods [:get :post :put :delete]
    :access-control-allow-credentials "true"))

(defroutes app-routes
           (OPTIONS "*" [] "")
           (GET "/" [] "Hello World")
           (GET "/id/default-game" [] game-api/get-game-id)
           (GET "/game" [] game-api/get-game-id)
           (GET "/game/:game-id" [] game-api/get)
           (GET "/game/:game-id/board" [] board-api/get)
           (GET "/game/:game-id/player" [] player-api/get-all)
           (GET "/game/:game-id/player/:player-id" [] player-api/get)
           (POST "/game" [] game-api/add!)
           (POST "/game/:game-id/player/:player-id/move/:player-piece-index" [] move-api/add!)
           (GET "/game/:game-id/move/:id" [] move-api/get)
           (GET "/game/:game-id/move" [] move-api/get-all)
           (OPTIONS "/game/:game-id/player/:player-id/move" [] "")
           (DELETE "/game/:game-id" [] game-api/delete!)
           (DELETE "/game/:game-id/move/:id" [] move-api/delete!)
           (route/not-found "Not Found"))

(def app (middleware-json/wrap-json-response
           (wrap-cors-for-client
              (middleware-default/wrap-defaults app-routes
                                                  (assoc-in middleware-default/api-defaults
                                                              [:responses, :content-types]
                                                              false)))

           {:pretty-print true}))

