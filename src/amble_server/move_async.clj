(ns amble-server.move-async
  (:require [clojure.core.async :refer [go, go-loop] :as async]
            [ring.adapter.jetty9 :as jetty])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]))


(def log (. LogManager getLogger "amble-server.move-async"))

(def websockets-path "/move/async")

(def subscribers (atom {}))

(def latest-move-index-chan (async/chan))

(defn add-subscriber! [subscriber]
  (.info log "Adding subscriber to subscribers (count, '{}'), '{}'", (count (deref subscribers)) subscriber)
  (swap! subscribers assoc (:subscriber-id subscriber) subscriber)
  (.info log "Added subscriber to subscribers (count, '{}'), '{}'", (count (deref subscribers)) subscriber))

(defn remove-subscriber! [subscriber]
  (.info log "Removing subscriber from subscribers (count, '{}'), '{}'", (count (deref subscribers)) subscriber)
  (swap! subscribers dissoc (:subscriber-id subscriber))
  (.debug log "Removed subscriber from subscribers (count, '{}'), '{}'", (count (deref subscribers)) subscriber))
  ;; (swap! subscribers #(filter (= % subscriber))))

(defn notify-subscribers! [latest-move-index]
  (doseq [subscriber-key (keys (deref subscribers))]
    (try
      (.info log "Updating " (count (deref subscribers)) " subscribers.")
      (let [{:keys [update-subscriber!]} ((deref subscribers)
                                          subscriber-key)]
        (update-subscriber! (str latest-move-index)))
      (catch Exception e
        (do
          (.error log "Something bad happened while trying to notify subscriber.", e)
          (when-let [subscriber-to-remove ((deref subscribers))]
                                           subscriber-key 
            (remove-subscriber! subscriber-to-remove)))))))   

(defn subscriber 
  "Creates a subscriber instance.
   Its input should be the value that the client cares about as notification input,
   currently the number index of the latest move made."
  [ws]
  {:subscriber-id 
   (.hashCode ws)
   :update-subscriber!
   (partial jetty/send! ws)})

(defn on-connect [ws & args]
  (.info log "New move async connection!")
  (add-subscriber! (subscriber ws))
  nil)

(defn on-error [ws & args]
  (.info log "New move async error")
  (.debug log args)
  nil)

(defn on-close [ws & args]
  (.info log "Existing async channel to close.")
  (when-let [subscriber-to-remove 
             ((deref subscribers)
              (.hashCode ws))]
    (remove-subscriber! subscriber-to-remove))   
  nil)


(def handlers {:on-connect on-connect
               :on-error on-error 
               :on-text nil
               :on-close on-close
               :on-bytes nil})

;; Reads from the input chan and notifies subscribers.
(go-loop []
  (let [latest-move-index (async/<! latest-move-index-chan)]
    (.info log latest-move-index)
    (notify-subscribers! latest-move-index)
    (recur)))

(defn init! [latest-move-index-chan]
  (async/pipe latest-move-index-chan amble-server.move-async/latest-move-index-chan))
