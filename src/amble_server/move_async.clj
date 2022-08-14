(ns amble-server.move-async
  (:require [clojure.core.async :refer [go, go-loop] :as async]
            [ring.adapter.jetty9 :as jetty]
            [amble-server.resource.move :as move-resource])
  (:import [org.apache.logging.log4j Logger]
           [org.apache.logging.log4j LogManager]
           [java.lang Exception]))


(def log (. LogManager getLogger "amble-server.move-async"))

(def websockets-path "/move/async")

(def subscribers (atom {}))

(def latest-move-index-chan (async/chan))

(defn add-subscriber! [subscriber]
  (.info log (str "Adding subscriber to \"" (name (:game-id subscriber)) "\" subscriber list, current count, " (count ((:game-id subscriber) (deref subscribers))) "."))
  (swap! subscribers assoc-in [(:game-id subscriber) (:subscriber-id subscriber)] 
                              subscriber)
  (.info log (str "Added subscriber to \"" (name (:game-id subscriber)) "\" subscriber list, current count, " (count ((:game-id subscriber) (deref subscribers))) ".")))

(defn remove-subscriber! [subscriber]
  (.info log (str "Removing subscriber to \"" (name (:game-id subscriber)) "\" subscriber list, current count, " (count ((:game-id subscriber) (deref subscribers))) "."))
  ;; (swap! subscribers dissoc (:subscriber-id subscriber))
  ;; (update-in {:a {:b 0 :c 1}} [:a] dissoc :b)
  (swap! subscribers update-in [(:game-id subscriber)] dissoc (:subscriber-id subscriber))
  (.info log (str "Removed subscriber to \"" (name (:game-id subscriber)) "\" subscriber list, current count, " (count ((:game-id subscriber) (deref subscribers))) ".")))
  ;; (swap! subscribers #(filter (= % subscriber))))

(defn notify-subscribers! [latest-move-index]
  (let [
        move-latest (move-resource/get-by-rowid latest-move-index)  
        game-id (keyword (:game-id move-latest))
        move-game-subscribers (game-id (deref subscribers))]
    (doseq [subscriber-key (keys move-game-subscribers)]
      (try
        (.info log (str "Updating " (count move-game-subscribers) " subscribers, game, \"" game-id "\"."))
        (let [{:keys [update-subscriber!]} (move-game-subscribers
                                            subscriber-key)]
          (update-subscriber! (str (:id move-latest))))
        (catch Exception e
          (do
            (.error log "Something bad happened while trying to notify subscriber.", e)
            (when-let [subscriber-to-remove (move-game-subscribers
                                             subscriber-key)] 
              (remove-subscriber! subscriber-to-remove))))))))

(defn subscriber 
  "Creates a subscriber instance."
  [ws, game-id]
  {:game-id game-id
   :subscriber-id 
   (.hashCode ws)
   :update-subscriber!
   (partial jetty/send! ws)})

(defn game-id [ws]
  (keyword (first (.get (.getParameterMap (.getUpgradeRequest (.getSession ws)))
                        "game-id"))))

(defn on-connect [ws & args]
  (.info log "New move async connection!")
  (.info log (type ws))
  (if-let [game-id-from-query-param
           (game-id ws)]
    (add-subscriber! (subscriber ws game-id-from-query-param))
    (throw (new Exception "No game id available at start of websockets connection.")))
  nil)

(defn on-error [ws & args]
  (.info log "New move async error")
  (.debug log args)
  nil)

(defn on-close [ws & args]
  (.info log "Existing async channel to close.")
  (let [
        ;; _ (.info log (nil? (.getSession ws)))
        ;; _ (.info log ws)
        ;; _ (.info log (.getSession ws))
        ;; _ (.info log (.getUpgradeRequest (.getSession ws)))
        ;; _ (.info log (.getParameterMap (.getUpgradeRequest (.getSession ws))))
        ;; game-id (game-id ws)
        ;; _ (.info log game-id)
        ;; subscribers-by-game-id
        ;; ((deref subscribers)
        ;;  (game-id ws))
        ;; _ (.info log subscribers-by-game-id)
        ;; subscriber-by-ws
        ;; (subscribers-by-game-id
        ;;  (.hashCode ws))
        ;; _ (.info log subscriber-by-ws)]
        subscribers-all (mapcat (fn [[_ subscribers-by-game-id]]
                                  (.info log "also wtf")
                                  (.info log subscribers-by-game-id)
                                  (vals subscribers-by-game-id))
                                (deref subscribers))   
        _ (.info log "wuf")
        _ (.info log subscribers-all)
        _ (.info log (vec subscribers-all))
        subscriber-one
        (first (filter (fn [s]                         
                         (= (:subscriber-id s)
                            (.hashCode ws)))
                       subscribers-all))]      
    (when-let [subscriber-to-remove subscriber-one]
      (remove-subscriber! subscriber-to-remove)))   
  nil)


(def handlers {:on-connect on-connect
               :on-error on-error 
               :on-text nil
               :on-close on-close
               :on-bytes nil})

;; Reads from the input chan and notifies subscribers.
(go-loop []
  (let [latest-move-index (async/<! latest-move-index-chan)]
    (.info log (str "Posting latest move index, "
                    latest-move-index))
    (.info log latest-move-index)
    (notify-subscribers! latest-move-index)
    (recur)))

(defn init! [latest-move-index-chan]
  (async/pipe latest-move-index-chan amble-server.move-async/latest-move-index-chan))
