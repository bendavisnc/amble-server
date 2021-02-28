(ns amble-server.main
  (:require [ring.adapter.jetty9 :as jetty]
            [amble-server.handler :as amble-handler]
            [amble-server.async-resource.core :as async-resource])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.main"))

(defn -main [& args]
 (.info log "Starting websockets ready web server.")
 (jetty/run-jetty amble-handler/app {:port 3000
                                     ;:websockets {"/game/:game-id/player/:player-id/async"
                                     :websockets {async-resource/path
                                                  async-resource/handler-fns}}))




