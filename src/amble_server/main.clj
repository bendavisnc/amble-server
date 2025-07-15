(ns amble-server.main
  "Provides main entry point for initializing async websocket behavior and starting the web server."
  (:require
   [amble-server.config :as amble-config]
   [amble-server.db.move-trigger :as move-trigger]
   [amble-server.handler :as amble-handler]
   [amble-server.move-async :as move-async]
   [clojure.core.async :as async]
   [ring-debug-logging.core :refer [wrap-with-logger]]
   [ring.adapter.jetty9 :as jetty]
   [ring.middleware.reload :as reload])
  (:import
   (org.apache.logging.log4j LogManager)))

(def log (. LogManager getLogger "amble-server.main"))

(defn -main [& _]
  (let [_ (.info log "Setting up amble server.")
        latest-move-index-chan (async/chan)]
    (move-trigger/init! (fn [latest-move-index]
                          (async/put! latest-move-index-chan
                                      latest-move-index)))
    (move-async/init! latest-move-index-chan)
    (.info log "Starting websockets ready web server.")
    (jetty/run-jetty (reload/wrap-reload (wrap-with-logger amble-handler/app))
                     {:port        (Integer/parseInt amble-config/port)
                      :daemon?    true
                      :websockets {move-async/websockets-path
                                   move-async/handlers}})))
