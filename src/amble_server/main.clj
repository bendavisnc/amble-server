(ns amble-server.main
  "Provides main entry point for initializing async websocket behavior and starting the web server."
  (:require
   [amble-server.api.handler :as api-handler]
   [amble-server.config :as amble-config]
   [amble-server.db.db :as db]
   [amble-server.db.move-trigger.move-trigger :as move-trigger]
   [amble-server.move-async :as move-async]
   [clojure.core.async :as async]
   [ring-debug-logging.core :refer [wrap-with-logger]]
   [ring.adapter.jetty9 :as jetty])
  (:import
   (org.apache.logging.log4j LogManager))
  (:gen-class))

(def log (. LogManager getLogger "amble-server.main"))

(defn maybe-wrap-reload [handler]
  (if amble-config/devmode?
    (do
      (require 'ring.middleware.reload)
      (let [wrap-reload (resolve 'ring.middleware.reload/wrap-reload)]
        (wrap-reload handler)))
    handler))

(defn -main [& _]
  (let [_ (.info log "Setting up amble server.")
        latest-move-index-chan (async/chan)]
    (if amble-config/postgres?
      (.info log (format "Using postgres db, `%s`." amble-config/postgres-subname))
      (.info log "Using sqlite db."))
    (move-trigger/init! (fn [latest-move-index]
                          (async/put! latest-move-index-chan latest-move-index)))
    (move-async/init! latest-move-index-chan)
    (.info log "Starting websockets-ready web server.")
    (jetty/run-jetty (maybe-wrap-reload (wrap-with-logger api-handler/handler))
                     {:port        (Integer/parseInt amble-config/port)
                      :daemon?     true
                      :websockets  {move-async/websockets-path
                                    move-async/handlers}})))
