(ns amble-server.async-resource.core
  (:require [amble-server.async-resource.move :as async-move])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.async-resource.core"))

(defn on-connect [& args]
  (.info log "on-connect")
  (.info log args)
  (async-move/on-connect! nil, nil, nil))

(def handler-fns {:on-connnect on-connect})
                  ;:on-error on-error
                  ;:on-text on-text
                  ;:on-close on-close
                  ;:on-bytes on-bytes



;(def websockets-handler
;  {:on-connect on-connect
;   :on-error on-error
;   :on-text on-text
;   :on-close on-close
;   :on-bytes on-bytes})
;


