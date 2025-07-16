(ns amble-server.api.handler
  (:require
   [amble-server.api.board :as board-api]
   [amble-server.api.game :as game-api]
   [amble-server.api.move :as move-api]
   [amble-server.api.player :as player-api]
   [amble-server.config :as amble-config]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.defaults :as defaults]
   [ring.middleware.json :as middleware-json]))

(defn- wrap-cors-for-client [handler]
  (wrap-cors handler
    :access-control-allow-origin [(re-pattern (or amble-config/client-url
                                                  (throw (new Exception "`client-url` not set in environment variables."))))]
    :access-control-allow-methods [:get :post :put :delete :options]
    :access-control-allow-credentials (str true)))

(defroutes app-routes
  (OPTIONS "*" [] "")
  (GET "/ping" [] "Hello World")
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

(def handler
  (-> app-routes
      (defaults/wrap-defaults (assoc-in defaults/api-defaults
                                        [:responses :content-types] false))
      wrap-cors-for-client
      (middleware-json/wrap-json-response {:pretty-print true})))
