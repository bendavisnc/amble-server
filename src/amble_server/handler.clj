(ns amble-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]] 
            [amble-server.resource.game :as game]))

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
  (GET "/game/:game-id" [] (middleware-custom game/get-by-id))
  (GET "/game" [] (middleware-custom game/get-by-tag))
  (POST "/game" [] game/create)
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes api-defaults))


