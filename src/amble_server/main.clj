(ns amble-server.main
  "Provides main entry point for initializing async websocket behavior and starting the web server."
  (:require
    [amble-server.api.handler :as api-handler]
    [amble-server.config :as amble-config]
    [amble-server.db.move-trigger.move-trigger :as move-trigger]
    [amble-server.move-async :as move-async]
    [ring.middleware.cors :refer [wrap-cors]]
    [ring.middleware.defaults :as ring-middleware-defaults]
    [ring.middleware.json :as ring-middleware-json]
    [clojure.core.async :as async]
    #_[ring-debug-logging.core :refer [wrap-with-logger]]
    [ring.middleware.params :as ring-middleware-params]
    [ring.adapter.jetty :as jetty]
    [ring.websocket :as ring-websocket]
    [taoensso.timbre :as log])
  (:gen-class))

(defn- maybe-wrap-reload
  [handler]
  (if amble-config/devmode?
    (do
      (require 'ring.middleware.reload)
      (let [wrap-reload (resolve 'ring.middleware.reload/wrap-reload)]
        (wrap-reload handler)))
    handler))

(defn- wrap-cors-for-client
  [handler]
  (wrap-cors handler
             :access-control-allow-origin
             [(re-pattern
               (or amble-config/client-url
                   (throw (new
                           Exception
                           "`client-url` not set in environment variables."))))]
             :access-control-allow-methods [:get :post :put :delete :options]
             :access-control-allow-credentials (str true)))

(def http-handler
  ((comp maybe-wrap-reload
         wrap-cors-for-client
         #(ring-middleware-json/wrap-json-body % {:keywords? true})
         #(ring-middleware-json/wrap-json-response % {:pretty-print true})
         #(ring-middleware-defaults/wrap-defaults
           %
           (assoc-in ring-middleware-defaults/api-defaults
            [:responses :content-types]
            false)))
   api-handler/handler))

(def websockets-handler (ring-middleware-params/wrap-params move-async/handler))

(defn handler
  [req]
  (if (ring-websocket/upgrade-request? req)
    (websockets-handler req)
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
