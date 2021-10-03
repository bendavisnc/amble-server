(ns amble-server.async-resource.core
  (:require [amble-server.async-resource.move :as async-move]
            [ring.adapter.jetty9 :as jetty]
            [ring.util.codec]
            [ring.util.codec :as codec]
            [clojure.string :as str])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]
           (ring.adapter.jetty9.websocket WebSocketProtocol)
           (java.util.regex Pattern)))

(def log (. LogManager getLogger "amble-server.async-resource.core"))

(def path
  "/game/async")

(defn session-id [request-map]
  (let [headers-map (:headers request-map)
        cookie (get headers-map "cookie")
        ring-session-id (-> cookie
                            (str/split (re-pattern "ring-session="))
                            last)]
    (assert (not (nil? ring-session-id)))
    ring-session-id))

(defn on-connect [& args]
  (.info log "Websocket lifecycle method invoked.")
  (.info log (str "  "
                  ["on-connect", args]))

  (let [ws (first args)
        request-map (jetty/req-of ws)
        params (codec/form-decode (:query-string request-map))
        session-id (session-id request-map)
        game-id (get params "game-id")
        send-fn (partial jetty/send! ws)
        _ (assert (not (nil? game-id)))]
    (async-move/on-connect! game-id, session-id, send-fn)))

(defn on-error [& args]
  (.info log "Websocket lifecycle method invoked.")
  (.info log (str "  "
                  ["on-error", args])))

(defn on-text [& args]
  (.info log "Websocket lifecycle method invoked.")
  (.info log (str "  "
                  ["on-text", args])))

(defn on-close [& args]
  (.info log "Websocket lifecycle method invoked.")
  (.info log (str "  "
                  ["on-close", args])))

(defn on-bytes [& args]
  (.info log "Websocket lifecycle method invoked.")
  (.info log (str "  "
                  ["on-bytes", args])))

(def handler-fns {:on-connect on-connect
                  :on-error   on-error
                  :on-text    on-text
                  :on-close   on-close
                  :on-bytes   on-bytes})

;(def websockets-handler
;  {:on-connect on-connect
;   :on-error on-error
;   :on-text on-text
;   :on-close on-close
;   :on-bytes on-bytes})
;
