(ns amble-server.move-async
  "Handles websocket connections for move updates.
   Provides functionality to add and remove subscribers, notify them of new moves,
   and handle connection events."
  (:require
    [amble-server.resource.move :as move-resource]
    [clojure.core.async :as async :refer [go-loop]]
    [ring.websocket :as ring-websocket]
    [taoensso.timbre :as log])
  (:import
    [java.nio.channels ClosedChannelException]))

(def subscribers (atom {}))

(def latest-move-index-chan (async/chan))

(defn move-latest-with-retry
  [rowid]
  (let [move (move-resource/get-by-rowid rowid)]
    (if (nil? move)
      (let
        [_
         (log/debug
          ::move-latest-with-retry
          "Encountered problem with retrieving move for async sending out. \n Trying again one more time in less than a second.")
         _ (Thread/sleep 250)
         move-second-go (move-resource/get-by-rowid rowid)]
        (if (nil? move-second-go)
          (throw (ex-info
                  "Can't get latest move with rowid after retry attempt."
                  {:rowid rowid}))
          move-second-go))
      move)))

(defn add-subscriber!
  [subscriber]
  (log/info ::add-subscriber!
            "Adding subscriber."
            {:game-id (:game-id subscriber)
             :count   (count ((:game-id subscriber) (deref subscribers)))})
  (swap! subscribers assoc-in
    [(:game-id subscriber) (:subscriber-id subscriber)]
    subscriber)
  (log/info ::add-subscriber!
            "Added subscriber."
            {:game-id (:game-id subscriber)
             :count   (count ((:game-id subscriber) (deref subscribers)))}))

(defn remove-subscriber!
  [subscriber]
  (log/info ::remove-subscriber!
            "Removing subscriber."
            {:game-id (:game-id subscriber)
             :count   (count ((:game-id subscriber) (deref subscribers)))})
  (swap! subscribers update-in
    [(:game-id subscriber)]
    dissoc
    (:subscriber-id subscriber))
  (log/info ::remove-subscriber!
            "Removed subscriber."
            {:game-id (:game-id subscriber)
             :count   (count ((:game-id subscriber) (deref subscribers)))}))

(defn notify-subscribers!
  "Updates callback listeners of a game's latest move id."
  [latest-move-index]
  (try
    (let [_ (Thread/sleep 250) ;; todo, remove this line and figure out a
                               ;; way to ensure that the db transaction is
                               ;; successful already.
          move-latest (move-latest-with-retry latest-move-index)
          _ (assert (some? move-latest)
                    (str "move-latest is null, \"" move-latest "\"."))

          game-id     (keyword (:game-id move-latest))
          _ (assert (some? game-id)
                    (str "game-id is null, \"" game-id "\"."))
          move-game-subscribers (game-id (deref subscribers))]

      (assert
       (not (empty? move-game-subscribers))
       (str "Move game subscribers is empty, \"" move-game-subscribers "\"."))
      (doseq [subscriber-key (keys move-game-subscribers)]
        (try
          (log/info ::notify-subscribers!
                    "Updating subscribers."
                    {:game-id game-id
                     :count   (count move-game-subscribers)})

          (let [{:keys [update-subscriber!]} (move-game-subscribers
                                              subscriber-key)]
            (update-subscriber! (str (:id move-latest))))
          (catch Exception e
            (do
              (log/error
               ::notify-subscribers!
               "Something bad happened while trying to notify subscriber."
               e)
              (when-let [subscriber-to-remove (move-game-subscribers
                                               subscriber-key)]
                (remove-subscriber! subscriber-to-remove)))))))
    (catch Throwable e
      (log/error ::notify-subscribers!
                 "Something bad happened while trying to notify subscribers."
                 e))))


(defn subscriber
  "Creates a subscriber instance."
  [ws game-id]
  {:game-id game-id
   :subscriber-id
   (.hashCode ws)
   :update-subscriber!
   (partial ring-websocket/send ws)})

(defn on-connect
  [ws game-id]
  (log/info ::on-connect "New move async connection!" {:game-id game-id})
  (add-subscriber! (subscriber ws game-id))
  nil)

(defn find-subscriber-by-id
  [subscriber-id]
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
      (do (log/debug ::find-subscriber-by-id
                     "Can't find subscriber by id."
                     {:subscriber-id subscriber-id})
          nil))))

(defn on-close
  [ws & args]
  (log/info ::on-close "Existing async channel to close.")
  (when-let [error-message (some-> args
                                   second
                                   .getMessage)]
    (let [code (some-> args
                       first)]
      (log/error ::on-close
                 "Error occurred at websocket close"
                 {:code code :error-message error-message})))
  (if-let [subscriber-to-remove (find-subscriber-by-id (.hashCode ws))]
    (remove-subscriber! subscriber-to-remove)
    (log/debug ::on-close "No subscriber found to remove at close."))
  nil)

(defn on-error
  [ws e]
  (when (instance? ClosedChannelException e)
    (log/debug ::on-error "Encountered `ClosedChannelException` error."))
  (if-let [subscriber-to-remove (find-subscriber-by-id (.hashCode ws))]
    (remove-subscriber! subscriber-to-remove)
    (log/debug ::on-error "No subscriber found to remove at error"))
  (when-not (instance? ClosedChannelException e)
    (throw (ex-info "Encountered unexpected async error." {} e)))
  nil)

(defn handler
  [upgrade-request]
  (let [game-id (keyword (get (:query-params upgrade-request)
                              "game-id"))
        _ (when (nil? game-id)
            (log/error "No `game-id` found from websocket request."))
        provided-subprotocols (:websocket-subprotocols upgrade-request)
        websocket-listener
        {:ring.websocket/listener {:on-open  (fn [socket]
                                               (on-connect socket game-id)
                                               nil)
                                   :on-close on-close
                                   :on-error on-error}
         :ring.websocket/protocol (first provided-subprotocols)}]
    websocket-listener))

;; Reads from the input chan and notifies subscribers.
(go-loop []
  (let [latest-move-index (async/<! latest-move-index-chan)]
    (log/info (format "Posting latest move index, `%s`."
                      latest-move-index))
    (notify-subscribers! latest-move-index)
    (recur)))

(defn init!
  [latest-move-index-chan]
  (async/pipe latest-move-index-chan
              amble-server.move-async/latest-move-index-chan))
