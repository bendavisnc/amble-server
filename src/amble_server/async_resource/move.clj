(ns amble-server.async-resource.move)

(def connected-subscribers-atom (atom {}))

(defn on-connect! [game-id, player-id, send-fn]
  (swap! connected-subscribers-atom assoc [game-id, player-id] send-fn))

(defn post-announcement! [game-id, player-id, move-id]
  (if-let [subscriber-send-fn (apply (deref connected-subscribers-atom)
                                     [[game-id, player-id]])]
    (do
      (subscriber-send-fn move-id)
      (println (format "Announced move, \"%s\".", move-id)))
    ;else)
    (println (format "Missing subscriber for, \"%s\" while wanting to announce move, \"%s\".", [game-id, player-id], move-id))))
nil

