(ns amble-server.main
  (:require [ring.adapter.jetty9 :as jetty]
            [amble-server.handler :as amble-handler]
            [amble-server.remote-control :as remote-control]))

(defn -main [& args]
  (println "Starting websockets ready web server.")
  (jetty/run-jetty amble-handler/app {:port 3000
                                      :websockets {"/async"
                                                   remote-control/websockets-handler}}))




