(ns amble-server.async-resource.move
  (:require [ring.adapter.jetty9 :as jetty])
  (:import (org.apache.logging.log4j LogManager)))

(def log (. LogManager getLogger "amble-server.async-resource.move"))

(def connected-subscribers-atom (atom {}))

(defn on-connect! [game-id, subscriber-id, send-fn]
  (let []
    (swap! connected-subscribers-atom
           assoc-in
           [game-id, subscriber-id],
           send-fn)
    (.info log "Added subscriber to game.")
    (.info log (str "  "
                    [game-id, @connected-subscribers-atom]))))

(defn post-announcement! [game-id, player-id, move-id]
  (let [subscribers (get (deref connected-subscribers-atom)
                         game-id)
        _ (assert (not (empty? subscribers))
                  "No subscribers available for move announcement.")]
    (doseq [[subscriber-id, subscriber-send-fn] subscribers]
      (subscriber-send-fn (str {:move, [game-id, player-id, move-id]}))
      (.info log "Announced move to subscriber.")
      (.info log (str "  "
                      [move-id, subscriber-id])))))


