(ns amble-server.main
  "Provides main entry point for initializing async websocket behavior and starting the web server."
  (:require
   [amble-server.api.handler :as api-handler]
   [amble-server.config :as amble-config]
   [amble-server.db.move-trigger :as move-trigger]
   [amble-server.db.move-trigger-postgres :as move-trigger-postgres]
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
    (if amble-config/postgres?
      (do
        (.info log "Using Postgres db.")
        (move-trigger-postgres/init! (fn [latest-move-index]
                                       (async/put! latest-move-index-chan
                                                   latest-move-index))))
      (do
        (.info log "Using sqlite db.")
        (move-trigger/init! (fn [latest-move-index]
                              (async/put! latest-move-index-chan
                                          latest-move-index)))))
    (move-async/init! latest-move-index-chan)
    (.info log "Starting websockets ready web server.")
    (jetty/run-jetty (reload/wrap-reload (wrap-with-logger api-handler/handler))
                     {:port        (Integer/parseInt amble-config/port)
                      :daemon?    true
                      :websockets {move-async/websockets-path
                                   move-async/handlers}})))
