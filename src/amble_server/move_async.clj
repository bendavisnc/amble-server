(ns amble-server.move-async
  "Handles websocket connections for move updates.
   Provides functionality to add and remove subscribers, notify them of new moves,
   and handle connection events."
  (:require
   [amble-server.resource.move :as move-resource]
   [clojure.core.async :as async :refer [go go-loop]]
   [ring.adapter.jetty9 :as jetty])
  (:import
   (java.lang Exception)
   (org.apache.logging.log4j LogManager Logger)))

(def log (. LogManager getLogger "amble-server.move-async"))

(def websockets-path "/move/async")

(def subscribers (atom {}))

(def latest-move-index-chan (async/chan))

(defn move-latest-with-retry [rowid]
  (let [move (move-resource/get-by-rowid rowid)]
    (if (nil? move)
      (let [_ (.debug log "Encountered problem with retrieving move for async sending out. \n Trying again one more time in less than a second.")
            _ (Thread/sleep 250)
            move-second-go (move-resource/get-by-rowid rowid)]
        (if (nil? move-second-go)
          (throw (new Exception (str "Can't get lastest move with rowid, \"" rowid "\" after retry attempt.")))
          move-second-go))
      move)))

(defn add-subscriber! [subscriber]
  (.info log (str "Adding subscriber to \"" (name (:game-id subscriber)) "\" subscriber list, current count, " (count ((:game-id subscriber) (deref subscribers))) "."))
  (swap! subscribers assoc-in [(:game-id subscriber) (:subscriber-id subscriber)]
                              subscriber)
  (.info log (str "Added subscriber to \"" (name (:game-id subscriber)) "\" subscriber list, current count, " (count ((:game-id subscriber) (deref subscribers))) ".")))

(defn remove-subscriber! [subscriber]
  (.info log (str "Removing subscriber to \"" (name (:game-id subscriber)) "\" subscriber list, current count, " (count ((:game-id subscriber) (deref subscribers))) "."))
  (swap! subscribers update-in [(:game-id subscriber)] dissoc (:subscriber-id subscriber))
  (.info log (str "Removed subscriber to \"" (name (:game-id subscriber)) "\" subscriber list, current count, " (count ((:game-id subscriber) (deref subscribers))) ".")))

(defn notify-subscribers!
  "Updates callback listeners of a game's latest move id."
  [latest-move-index]
  (try
    (let [_ (Thread/sleep 250) ;; todo, remove this line and figure out a way to ensure that the db transaction is successful already.
          move-latest (move-latest-with-retry latest-move-index)
          _ (assert (not (nil? move-latest))
                    (str "move-latest is null, \"" move-latest "\"."))

          _ (.info log move-latest)
          game-id (keyword (:game-id move-latest))
          _ (assert (not (nil? game-id))
                    (str "game-id is null, \"" game-id "\"."))
          move-game-subscribers (game-id (deref subscribers))]

      (assert (not (empty? move-game-subscribers))
              (str "Move game subscribers is empty, \"" move-game-subscribers "\"."))
      (doseq [subscriber-key (keys move-game-subscribers)]
        (try
          (.info log (str "Updating " (count move-game-subscribers) " subscribers, game, \"" (name game-id) "\"."))
          (let [{:keys [update-subscriber!]} (move-game-subscribers
                                               subscriber-key)]
            (update-subscriber! (str (:id move-latest))))
          (catch Exception e
            (do
              (.error log "Something bad happened while trying to notify subscriber.", e)
              (when-let [subscriber-to-remove (move-game-subscribers
                                                subscriber-key)]
                (remove-subscriber! subscriber-to-remove)))))))
    (catch Throwable e
      (.error log "Something bad happened while trying to notify subscribers.", e))))

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
  (if-let [game-id-from-query-param
           (game-id ws)]
    (add-subscriber! (subscriber ws game-id-from-query-param))
    (throw (new Exception "No game id available at start of websockets connection.")))
  nil)

(defn find-subscriber-by-id [subscriber-id]
  (let [subscribers-all (mapcat (fn [[_ subscribers-by-game-id]]
                                    (vals subscribers-by-game-id))
                                (deref subscribers))
        subscriber-found
        (first (filter (fn [s]
                         (= (:subscriber-id s)
                            subscriber-id))
                       subscribers-all))]
    (if-let [subscriber subscriber-found]
      subscriber
      (do (.debug log (str "Can't find subscriber by id, \"" subscriber-id "\"."))
          nil))))

(defn on-close [ws & args]
  (.info log "Existing async channel to close.")
  (if-let [subscriber-to-remove (find-subscriber-by-id (.hashCode ws))]
    (remove-subscriber! subscriber-to-remove)
    (.debug log (str "No subscriber found to remove at close.")))
  nil)

(defn on-error [ws & args]
  (.error log (str "Encountered new async error. \n" (vec args)))
  (if-let [subscriber-to-remove (find-subscriber-by-id (.hashCode ws))]
    (remove-subscriber! subscriber-to-remove)
    (.debug log (str "No subscriber found to remove at error")))
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
