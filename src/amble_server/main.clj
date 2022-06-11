(ns amble-server.main
  (:require [ring.adapter.jetty9 :as jetty]
            [amble-server.handler :as amble-handler]
            [ring.middleware.reload :as reload]
            [amble-server.move-async :as move-async]
            [ring-debug-logging.core :refer [wrap-with-logger]])
  (:import [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.main"))

(defn -main [& args]
  (.info log "Starting websockets ready web server.")
  (jetty/run-jetty (reload/wrap-reload (wrap-with-logger amble-handler/app)) 
                   {:port       3000
                    ;:join?      true
                    :daemon?    true
                    :websockets {move-async/websockets-path
                                 move-async/handlers}}))



