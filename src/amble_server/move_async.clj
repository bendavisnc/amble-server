(ns amble-server.move-async
  (:require [clojure.core.async :refer [go, go-loop] :as async])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.move-async"))

(def websockets-path "/move/async")

(def latest-move-index-chan (async/chan))

;; Reads from the input chan and notifies subscribers.
(go-loop []
  (let [latest-move-index (async/<! latest-move-index-chan)]
    (.info log "Hey neat, need to come back to.")
    (.info log latest-move-index)
    (recur)))

(defn on-connect [ws & args]
  (.info log "New move async connection!")
  (.debug log args)
  nil)

(defn on-error [ws & args]
  (.info log "New move async error")
  (.debug log args)
  nil)


(def handlers {:on-connect on-connect
               :on-error on-error 
               :on-text nil
               :on-close nil
               :on-bytes nil})

(defn init! [latest-move-index-chan]
  (async/pipe latest-move-index-chan amble-server.move-async/latest-move-index-chan))

