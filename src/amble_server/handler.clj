(ns amble-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [amble-server.services.game :as game]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/game" [] game/create)
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))
