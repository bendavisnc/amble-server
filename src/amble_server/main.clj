(ns amble-server.main
  (:require [ring.adapter.jetty9 :as jetty]
            [amble-server.handler :as amble-handler]
            [amble-server.async-resource.core :as async-resource-core]))

(defn -main [& args]
  (println "Starting websockets ready web server.")
  (jetty/run-jetty amble-handler/app {:port 3000
                                      ;:websockets {"/game/:game-id/player/:player-id/async"
                                      :websockets {"/async"
                                                   async-resource-core/handler-fns}}))




