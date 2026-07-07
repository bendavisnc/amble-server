(ns amble-server.main
  "Provides main entry point for initializing async websocket behavior and starting the web server."
  (:require
    [amble-server.api.handler :as api-handler]
    [amble-server.config :as amble-config]
    [amble-server.db.move-trigger.move-trigger :as move-trigger]
    [amble-server.move-async :as move-async]
    [clojure.core.async :as async]
    [ring.adapter.jetty :as jetty]
    #_[ring-debug-logging.core :refer [wrap-with-logger]]
    [ring.middleware.params :as ring-middleware-params]
    [ring.websocket :as ring-websocket]
    [taoensso.timbre :as log])
  (:gen-class))

(defn maybe-wrap-reload
  [handler]
  (if amble-config/devmode?
    (do
      (require 'ring.middleware.reload)
      (let [wrap-reload (resolve 'ring.middleware.reload/wrap-reload)]
        (wrap-reload handler)))
    handler))

(def http-handler
  (-> api-handler/handler
      ;; wrap-with-logger
      maybe-wrap-reload))

(defn handler
  [req]
  (if (ring-websocket/upgrade-request? req)
    ((ring-middleware-params/wrap-params move-async/handler) req)
    (http-handler req)))

(defn -main
  [& _]
  (let [_ (log/info ::main "Setting up amble server.")
        latest-move-index-chan (async/chan)
        _ (log/info ::main "Starting websockets-ready web server.")
        server (jetty/run-jetty
                handler
                {:port            (Integer/parseInt amble-config/port)
                 :daemon?         false
                 :join?           false
                 :ws-idle-timeout 300000})] ;; todo, investigate best value
    ;; and error handling when timeout exceptions happen


    (move-trigger/init! (fn [latest-move-index]
                          (async/put! latest-move-index-chan
                                      latest-move-index)))
    (move-async/init! latest-move-index-chan)

    (log/info ::main
              "Amble server started."
              {:port amble-config/port
               :db   (if amble-config/postgres? :postgres :sqlite)})

    (.join server)))
