(ns amble-server.move-async
  (:require [clojure.core.async :refer [go, go-loop] :as async]
            [ring.adapter.jetty9 :as jetty])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.move-async"))

(def websockets-path "/move/async")

(def subscribers (atom []))

(def latest-move-index-chan (async/chan))

(defn remove-subscriber! [subscriber]
  (.info log "Removing subscriber from subscribers (count, '{}'), '{}'", (count (deref subscribers)) subscriber)
  (swap! subscribers (fn [subs]
                       (remove #{subscriber} subs)))
                      ;;  (filter (fn [s]
                                ;;  (= subscriber s)
                              ;;  subs]))
  (.debug log "Removed subscriber from subscribers (count, '{}'), '{}'", (count (deref subscribers)) subscriber))
  ;; (swap! subscribers #(filter (= % subscriber))))

(defn notify-subscribers! [latest-move-index]
  (doseq [subscriber @subscribers]
    (try
      (subscriber (str latest-move-index))
      (catch Exception e
        (do
          (.error log "Something bad happened while trying to notify subscriber.", e)
          (remove-subscriber! subscriber))))))
                    
;; Reads from the input chan and notifies subscribers.
(go-loop []
  (let [latest-move-index (async/<! latest-move-index-chan)]
    (.info log latest-move-index)
    (notify-subscribers! latest-move-index)
    (recur)))

(defn on-connect [ws & args]
  (.info log "New move async connection!")
  (.debug log args)
  (.info log (format "Currently there's %d subscribers."
                     (count (deref subscribers))))  
  (swap! subscribers conj (partial jetty/send! ws))
  (.info log (format "Now there's %d subscribers."
                     (count (deref subscribers))))  
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

