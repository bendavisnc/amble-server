(ns amble-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [amble-server.services.game :as game]
            [ring.middleware.cors :as mcors]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/game/:game-id" [] game/get-by-id)
  (POST "/game" [] game/create)
  (route/not-found "Not Found"))

(def app
  (mcors/wrap-cors (wrap-defaults app-routes api-defaults)
                   :access-control-allow-origin ["http://localhost:3001"]
                   :access-control-allow-headers ["Content-Type"]))


