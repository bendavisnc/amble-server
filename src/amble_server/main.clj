(ns amble-server.main
  (:require [ring.adapter.jetty9 :as jetty]
            [amble-server.handler :as amble-handler]
            [amble-server.config :as amble-config] 
            [clojure.core.async :as async]
            [ring.middleware.reload :as reload]
            [amble-server.move-async :as move-async]
            [amble-server.db.move-trigger :as move-trigger]
            [ring-debug-logging.core :refer [wrap-with-logger]])
  (:import [org.apache.logging.log4j LogManager]))

(def log (. LogManager getLogger "amble-server.main"))

(defn -main [& args]
  (let [_ (.info log "Setting up amble server.")
        latest-move-index-chan (async/chan)]
    (move-trigger/init! (fn [latest-move-index] 
                          (async/put! latest-move-index-chan
                                      latest-move-index))) 
    (move-async/init! latest-move-index-chan)
    (.info log "Starting websockets ready web server.")
    (jetty/run-jetty (reload/wrap-reload (wrap-with-logger amble-handler/app)) 
                    ;;  {:port       (amble-config/port)
                     {:port        (Integer/parseInt amble-config/port)
                      ;:join?      true
                      :daemon?    true
                      :websockets {move-async/websockets-path
                                   move-async/handlers}})))



