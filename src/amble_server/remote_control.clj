(ns amble-server.remote-control
  (:import (amble_server.async GameSubscriber)))

(defn post-move-announcement!
  "Asynchronously send out move notification to all game subscribers."
  [game-id, player-id, move-id]
  (doseq [subscriber [(GameSubscriber/gameSubscribers game-id)]]
    (do
      (println "Hey I'm a game subscriber to post moves to!")
      (println subscriber)))

  nil)

(defn add-game-subscriber! [game-id, player-id]
  (GameSubscriber/addGameSubscriber game-id, player-id)
  nil)

(defn- on-connect [session]
  (println "Websocket on connect invoked."))

(defn- on-close [session code reason]
  (println "Websocket on close invoked."))

(defn- on-text [session message]
  (println "Websocket on text invoked."))

(defn- on-bytes [session payload offset len]
  (println "Websocket on bytes invoked.")
  nil)

(defn- on-error [session e]
  (println "Websocket on error invoked.")
  (.printStackTrace e)
  nil)

(def websockets-handler
  {:on-connect on-connect
   :on-error on-error
   :on-text on-text
   :on-close on-close
   :on-bytes on-bytes})

